package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Md5Util;
import com.easycloud.entity.InviteLog;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.mapper.InviteLogMapper;
import com.easycloud.mapper.PaymentOrderMapper;
import com.easycloud.mapper.PointMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 支付服务：易支付协议（MD5 签名）下单、异步回调验签入账、退款
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final DateTimeFormatter ORDER_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final long MAX_AMOUNT_FEN = 1_000_000L;
    private static final Random RANDOM = new Random();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final PaymentOrderMapper paymentOrderMapper;
    private final PointMapper pointMapper;
    private final ConfigService configService;
    private final UserService userService;
    private final PointService pointService;
    private final InviteService inviteService;
    private final InviteLogMapper inviteLogMapper;

    @Transactional
    public Map<String, Object> createOrder(Long uid, BigDecimal amount, String payType, String inviteCode) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额无效");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.movePointRight(2).longValue() > MAX_AMOUNT_FEN) {
            throw new RuntimeException("单笔金额超出限制");
        }
        if (!"wxpay".equals(payType) && !"qqpay".equals(payType)) {
            payType = "wxpay";
        }
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(generateOrderNo());
        order.setUid(uid);
        order.setAmount(amount);
        order.setPayType(payType);
        order.setStatus(PaymentOrder.STATUS_PENDING);
        order.setInviteCode(inviteCode);
        order.setCreateTime(LocalDateTime.now());
        paymentOrderMapper.insert(order);
        return callGatewayAndUpdateOrder(order, payType, amount);
    }

    private Map<String, Object> callGatewayAndUpdateOrder(PaymentOrder order, String payType, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("amount", amount);
        result.put("payType", payType);

        String gatewayUrl = configService.getSetting("pay_apiurl");
        String appId = configService.getSetting(payType + "_appid");
        String appKey = configService.getSetting(payType + "_key");
        if (!StringUtils.hasText(gatewayUrl) || !StringUtils.hasText(appId) || !StringUtils.hasText(appKey)) {
            log.warn("支付网关未配置（pay_apiurl / appid / key），订单 {} 保持待支付", order.getOrderNo());
            result.put("payUrl", "");
            result.put("configured", false);
            return result;
        }
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("pid", appId);
            params.put("type", payType);
            params.put("out_trade_no", order.getOrderNo());
            params.put("notify_url", getNotifyUrl(payType));
            params.put("name", orDefault(configService.getSetting("sitename"), "EasyCloud") + "余额充值");
            params.put("money", amount.toPlainString());
            params.put("sign", generateSign(params, appKey));
            params.put("sign_type", "MD5");

            StringBuilder form = new StringBuilder();
            params.forEach((k, v) -> form.append(k).append('=').append(URLEncoder.encode(v, StandardCharsets.UTF_8)).append('&'));
            String target = gatewayUrl.endsWith("/") ? gatewayUrl + "mapi.php" : gatewayUrl + "/mapi.php";
            String responseBody = httpPost(target, form.substring(0, form.length() - 1));
            String payUrl = extractJsonField(responseBody, "payurl");
            if (!StringUtils.hasText(payUrl)) {
                payUrl = extractJsonField(responseBody, "qrcode");
            }
            result.put("payUrl", payUrl == null ? "" : payUrl);
            result.put("configured", true);
            if (!StringUtils.hasText(payUrl)) {
                order.setStatus(PaymentOrder.STATUS_FAILED);
                paymentOrderMapper.updateById(order);
                log.warn("支付网关下单失败 order={} resp={}", order.getOrderNo(), responseBody);
            }
        } catch (Exception e) {
            log.error("支付网关请求异常 order={}: {}", order.getOrderNo(), e.getMessage());
            result.put("payUrl", "");
            result.put("configured", true);
        }
        return result;
    }

    public Map<String, String> wxpayNotify(Map<String, String> params) {
        return handlePayNotify("wxpay", params);
    }

    public Map<String, String> qqpayNotify(Map<String, String> params) {
        return handlePayNotify("qqpay", params);
    }

    private Map<String, String> handlePayNotify(String payType, Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        String appKey = configService.getSetting(payType + "_key");
        if (!StringUtils.hasText(appKey) || !verifySign(params, appKey)) {
            log.warn("支付回调验签失败 payType={} orderNo={}", payType, params.get("out_trade_no"));
            response.put("status", "fail");
            return response;
        }
        String orderNo = params.get("out_trade_no");
        PaymentOrder order = getOrderByOrderNo(orderNo);
        if (order == null) {
            response.put("status", "fail");
            return response;
        }
        if (StringUtils.hasText(params.get("money"))) {
            try {
                BigDecimal money = new BigDecimal(params.get("money")).setScale(2, RoundingMode.HALF_UP);
                if (money.compareTo(order.getAmount()) != 0) {
                    log.warn("支付回调金额不一致 order={} expect={} actual={}", orderNo, order.getAmount(), money);
                    response.put("status", "fail");
                    return response;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        int affected = paymentOrderMapper.markAsPaid(orderNo, params.get("trade_no"), LocalDateTime.now());
        if (affected > 0) {
            rechargeUserBalance(order.getUid(), order.getAmount());
            applyInviteRebate(order, params);
        }
        response.put("status", "success");
        return response;
    }

    private void rechargeUserBalance(Long uid, BigDecimal amount) {
        userService.updateRmb(uid, amount);
        pointService.addPoint(uid, amount, null, "recharge");
        log.info("充值入账 uid={} amount={}", uid, amount);
    }

    private boolean isInviteRebateOpen() {
        return "1".equals(configService.getSetting("invite_rebate_open"));
    }

    private void applyInviteRebate(PaymentOrder order, Map<String, String> params) {
        try {
            if (!isInviteRebateOpen() || !StringUtils.hasText(order.getInviteCode())) {
                return;
            }
            com.easycloud.entity.User inviter = userService.getByInviteCode(order.getInviteCode());
            if (inviter == null || inviter.getUid().equals(order.getUid())) {
                return;
            }
            BigDecimal rate;
            try {
                rate = new BigDecimal(orDefault(configService.getSetting("invite_rebate_rate"), "5"));
            } catch (NumberFormatException e) {
                rate = new BigDecimal("5");
            }
            BigDecimal rebate = order.getAmount().multiply(rate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (rebate.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            userService.updateRmb(inviter.getUid(), rebate);
            inviteService.addInviteLog(inviter.getUid(), params.getOrDefault("qq", ""),
                    "rebate", rebate, "充值返利:" + order.getOrderNo());
            log.info("邀请返利发放 inviter={} order={} rebate={}", inviter.getUid(), order.getOrderNo(), rebate);
        } catch (Exception e) {
            log.error("邀请返利发放失败 order={}: {}", order.getOrderNo(), e.getMessage());
        }
    }

    private void rollbackInviteRebate(String orderNo) {
        try {
            List<InviteLog> logs = inviteLogMapper.selectList(new LambdaQueryWrapper<InviteLog>()
                    .eq(InviteLog::getBz, "充值返利:" + orderNo));
            for (InviteLog record : logs) {
                if (record.getMoney() != null && record.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                    userService.updateRmb(record.getUid(), record.getMoney().negate());
                }
                inviteLogMapper.deleteById(record.getId());
            }
        } catch (Exception e) {
            log.error("邀请返利回滚失败 order={}: {}", orderNo, e.getMessage());
        }
    }

    public IPage<PaymentOrder> getOrderList(Long uid, int page, int size) {
        return paymentOrderMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getUid, uid)
                        .orderByDesc(PaymentOrder::getCreateTime));
    }

    public IPage<PaymentOrder> getAllOrderList(int page, int size, String status) {
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(PaymentOrder::getStatus, status);
        }
        wrapper.orderByDesc(PaymentOrder::getCreateTime);
        return paymentOrderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public PaymentOrder getOrderStatus(String orderNo) {
        return getOrderByOrderNo(orderNo);
    }

    public PaymentOrder getOrderByOrderNo(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            return null;
        }
        return paymentOrderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>()
                .eq(PaymentOrder::getOrderNo, orderNo).last("LIMIT 1"));
    }

    @Transactional
    public boolean refundOrder(String orderNo) {
        PaymentOrder order = getOrderByOrderNo(orderNo);
        if (order == null || !PaymentOrder.STATUS_PAID.equals(order.getStatus())) {
            return false;
        }
        callRefundGateway(order);
        int affected = paymentOrderMapper.markRefunded(orderNo);
        if (affected == 0) {
            return false;
        }
        userService.updateRmb(order.getUid(), order.getAmount().negate());
        rollbackInviteRebate(orderNo);
        log.info("订单退款完成 order={} uid={}", orderNo, order.getUid());
        return true;
    }

    private void callRefundGateway(PaymentOrder order) {
        try {
            String gatewayUrl = configService.getSetting("pay_apiurl");
            String appKey = configService.getSetting(order.getPayType() + "_key");
            if (!StringUtils.hasText(gatewayUrl) || !StringUtils.hasText(appKey)) {
                return;
            }
            Map<String, String> params = new TreeMap<>();
            params.put("appid", orDefault(configService.getSetting(order.getPayType() + "_appid"), ""));
            params.put("out_trade_no", order.getOrderNo());
            params.put("money", order.getAmount().toPlainString());
            params.put("sign", generateSign(params, appKey));
            StringBuilder form = new StringBuilder();
            params.forEach((k, v) -> form.append(k).append('=').append(URLEncoder.encode(v, StandardCharsets.UTF_8)).append('&'));
            String target = gatewayUrl.endsWith("/") ? gatewayUrl + "refund.php" : gatewayUrl + "/refund.php";
            httpPost(target, form.substring(0, form.length() - 1));
        } catch (Exception e) {
            log.error("退款网关请求失败 order={}: {}", order.getOrderNo(), e.getMessage());
        }
    }

    /**
     * 易支付协议为单向 HTTPS，无需商户证书双向认证
     */
    private javax.net.ssl.SSLContext buildMchSslContext(String appid, String mchid) throws Exception {
        return javax.net.ssl.SSLContext.getDefault();
    }

    private Map<String, Object> callPayGateway(String url, String form, long totalFen) throws Exception {
        String body = httpPost(url, form);
        Map<String, Object> result = new HashMap<>();
        result.put("body", body);
        result.put("payurl", extractJsonField(body, "payurl"));
        return result;
    }

    /**
     * 易支付签名：参数按 key 字典序拼接 k=v& 后追加 key=密钥，取 MD5 小写（排除 sign/sign_type）
     */
    public String generateSign(Map<String, String> params, String appKey) {
        TreeMap<String, String> sorted = new TreeMap<>(params);
        sorted.remove("sign");
        sorted.remove("sign_type");
        StringBuilder sb = new StringBuilder();
        sorted.forEach((k, v) -> {
            if (StringUtils.hasText(v)) {
                sb.append(k).append('=').append(v).append('&');
            }
        });
        sb.append("key=").append(appKey);
        return md5(sb.toString());
    }

    private boolean verifySign(Map<String, String> params, String appKey) {
        String sign = params.get("sign");
        if (!StringUtils.hasText(sign)) {
            return false;
        }
        return sign.equalsIgnoreCase(generateSign(params, appKey));
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(ORDER_NO_FMT) + String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getNotifyUrl(String payType) {
        String base = configService.getSetting("pay_notify_url");
        if (!StringUtils.hasText(base)) {
            return "";
        }
        return base.endsWith("/") ? base + "api/pay/" + payType + "/notify" : base + "/api/pay/" + payType + "/notify";
    }

    private String httpPost(String url, String form) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form == null ? "" : form))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String md5(String input) {
        return Md5Util.md5(input);
    }

    private String extractJsonField(String json, String key) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        String search = new StringBuilder().append('"').append(key).append('"').toString();
        int k = json.indexOf(search);
        if (k < 0) {
            return null;
        }
        int colon = json.indexOf(':', k + search.length());
        if (colon < 0) {
            return null;
        }
        int start = json.indexOf('"', colon);
        if (start < 0) {
            return null;
        }
        int end = json.indexOf('"', start + 1);
        if (end < 0) {
            return null;
        }
        return json.substring(start + 1, end);
    }

    private String orDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
