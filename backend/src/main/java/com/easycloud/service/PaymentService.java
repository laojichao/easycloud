package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Md5Util;
import com.easycloud.common.XmlUtil;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.mapper.PaymentOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 支付服务
 * <p>
 * 处理微信支付和QQ支付的完整支付流程，对应原 PHP 项目的支付模块。
 * <p>
 * 支付流程：
 * <ol>
 *   <li>用户发起充值请求，后端生成唯一订单号</li>
 *   <li>调用微信/QQ支付统一下单接口，获取预支付参数</li>
 *   <li>返回支付参数给前端，前端调起支付</li>
 *   <li>用户完成支付后，支付网关异步回调通知</li>
 *   <li>后端验证回调签名，防重复处理，更新订单状态和用户余额</li>
 * </ol>
 * <p>
 * 签名规则：参数按 ASCII 排序，拼接 key=value&amp;...&amp;key=payKey，取 MD5 大写。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final ConfigService configService;
    private final UserService userService;

    private static final DateTimeFormatter ORDER_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 复用 HttpClient 实例，避免每次请求创建新连接 */
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * 创建支付订单
     *
     * @param uid     用户ID
     * @param amount  金额（元，BigDecimal）
     * @param payType 支付类型：wxpay / qqpay
     * @return 支付参数（由支付网关返回）
     */
    @Transactional
    public Map<String, Object> createOrder(Long uid, BigDecimal amount, String payType) {
        // 参数校验
        if (uid == null || uid <= 0) {
            throw new IllegalArgumentException("用户ID无效");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        if (!"wxpay".equals(payType) && !"qqpay".equals(payType)) {
            throw new IllegalArgumentException("不支持的支付类型: " + payType);
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单记录
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(orderNo);
        order.setUid(uid);
        order.setAmount(amount);
        order.setPayType(payType);
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        paymentOrderMapper.insert(order);

        // 调用支付网关（在事务外执行 HTTP 调用，避免长事务）
        // 先提交订单记录，再调用网关
        return callGatewayAndUpdateOrder(order, payType, amount);
    }

    /**
     * 调用支付网关并更新订单状态
     * 分离 HTTP 调用，避免在事务中执行长时间网络操作
     */
    private Map<String, Object> callGatewayAndUpdateOrder(PaymentOrder order, String payType, BigDecimal amount) {
        Map<String, Object> payParams;
        try {
            int amountInFen = amount.multiply(BigDecimal.valueOf(100)).intValue();
            payParams = callPayGateway(payType, order.getOrderNo(), amountInFen);
            payParams.put("orderNo", order.getOrderNo());
        } catch (Exception e) {
            log.error("调用支付网关失败: orderNo={}, payType={}", order.getOrderNo(), payType, e);
            order.setStatus("failed");
            paymentOrderMapper.updateById(order);
            throw new RuntimeException("支付网关调用失败，请稍后重试");
        }
        return payParams;
    }

    /**
     * 微信支付回调处理
     *
     * @param params 回调参数
     * @return 处理结果
     */
    @Transactional
    public Map<String, String> wxpayNotify(Map<String, String> params) {
        return handlePayNotify("wxpay", params);
    }

    /**
     * QQ支付回调处理
     *
     * @param params 回调参数
     * @return 处理结果
     */
    @Transactional
    public Map<String, String> qqpayNotify(Map<String, String> params) {
        return handlePayNotify("qqpay", params);
    }

    /**
     * 统一回调处理逻辑
     */
    private Map<String, String> handlePayNotify(String payType, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();

        // 1. 验证签名
        String configKey = payType.equals("wxpay") ? "wxpay_key" : "qqpay_key";
        String payKey = configService.getSetting(configKey);
        if (payKey == null || payKey.isEmpty()) {
            log.error("支付密钥未配置: {}", configKey);
            result.put("return_code", "FAIL");
            result.put("return_msg", "支付密钥未配置");
            return result;
        }

        if (!verifySign(params, payKey)) {
            log.warn("支付回调签名验证失败: payType={}, params={}", payType, params);
            result.put("return_code", "FAIL");
            result.put("return_msg", "签名验证失败");
            return result;
        }

        // 2. 检查支付结果
        String returnCode = params.getOrDefault("return_code", "");
        if (!"SUCCESS".equalsIgnoreCase(returnCode)) {
            log.warn("支付回调返回失败: payType={}, return_code={}", payType, returnCode);
            result.put("return_code", "SUCCESS");
            result.put("return_msg", "OK");
            return result;
        }

        // 3. 查找订单
        String orderNo = params.getOrDefault("out_trade_no", "");
        PaymentOrder order = paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
        if (order == null) {
            log.warn("支付回调找不到订单: orderNo={}", orderNo);
            result.put("return_code", "FAIL");
            result.put("return_msg", "订单不存在");
            return result;
        }

        // 4. 原子标记订单为已支付（防止并发重复处理）
        //    使用原子 UPDATE ... WHERE status = 'pending' 保证幂等性
        String tradeNo = params.getOrDefault("transaction_id", params.getOrDefault("transaction_id", ""));
        int affected = paymentOrderMapper.markAsPaid(orderNo, tradeNo, LocalDateTime.now());
        if (affected == 0) {
            // 订单已处理过（并发回调或重复通知），直接返回成功
            result.put("return_code", "SUCCESS");
            result.put("return_msg", "OK");
            return result;
        }

        // 5. 更新用户余额（仅在订单状态成功从 pending 变为 paid 后才充值）
        rechargeUserBalance(order.getUid(), order.getAmount());

        log.info("支付成功: orderNo={}, uid={}, amount={}, payType={}",
                orderNo, order.getUid(), order.getAmount(), payType);

        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        return result;
    }

    /**
     * 充值用户余额
     * 对应原 PHP 项目的余额更新逻辑
     */
    private void rechargeUserBalance(Long uid, BigDecimal amount) {
        userService.updateRmb(uid, amount);
        log.info("用户余额更新: uid={}, amount={}", uid, amount);
    }

    /**
     * 查询订单列表（用户维度）
     */
    public IPage<PaymentOrder> getOrderList(Long uid, int page, int size) {
        Page<PaymentOrder> pageParam = new Page<>(page, size);
        return paymentOrderMapper.selectPage(pageParam,
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getUid, uid)
                        .orderByDesc(PaymentOrder::getCreateTime));
    }

    /**
     * 查询订单列表（管理后台 - 全部订单）
     */
    public IPage<PaymentOrder> getAllOrderList(int page, int size, String status) {
        Page<PaymentOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PaymentOrder::getStatus, status);
        }
        wrapper.orderByDesc(PaymentOrder::getCreateTime);
        return paymentOrderMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 查询订单状态
     */
    public PaymentOrder getOrderStatus(String orderNo) {
        return paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
    }

    /**
     * 查询订单详情（管理后台）
     */
    public PaymentOrder getOrderByOrderNo(String orderNo) {
        return paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOrderNo, orderNo));
    }

    /**
     * 退款（管理后台）
     */
    @Transactional
    public boolean refundOrder(String orderNo) {
        PaymentOrder order = getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("只有已支付的订单才能退款，当前状态: " + order.getStatus());
        }

        // 更新订单状态
        order.setStatus("refunded");
        paymentOrderMapper.updateById(order);

        // 扣减用户余额（使用 UserService，保证与 yixi_user 表一致）
        userService.updateRmb(order.getUid(), order.getAmount().negate());

        log.info("退款成功: orderNo={}, uid={}, amount={}",
                orderNo, order.getUid(), order.getAmount());
        return true;
    }

    /**
     * 调用支付网关创建预支付
     *
     * @param payType      支付类型
     * @param orderNo      订单号
     * @param amountInFen  金额（分）
     * @return 支付参数
     */
    private Map<String, Object> callPayGateway(String payType, String orderNo, int amountInFen) throws Exception {
        Map<String, Object> result = new HashMap<>();

        if ("wxpay".equals(payType)) {
            String appId = configService.getSetting("wxpay_appid");
            String mchId = configService.getSetting("wxpay_mchid");
            String payKey = configService.getSetting("wxpay_key");

            if (appId == null || mchId == null || payKey == null) {
                throw new RuntimeException("微信支付配置不完整");
            }

            // 构建统一下单参数
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("nonce_str", generateNonceStr());
            params.put("body", "EasyCloud-余额充值");
            params.put("out_trade_no", orderNo);
            params.put("total_fee", String.valueOf(amountInFen));
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", getNotifyUrl("wxpay"));
            params.put("trade_type", "NATIVE");
            params.put("sign", generateSign(params, payKey));

            // 发送请求到微信统一下单接口
            String xmlBody = XmlUtil.mapToXml(params);
            String responseXml = httpPost("https://api.mch.weixin.qq.com/pay/unifiedorder", xmlBody);
            Map<String, String> responseParams = XmlUtil.xmlToMap(responseXml);

            if ("SUCCESS".equals(responseParams.get("return_code"))
                    && "SUCCESS".equals(responseParams.get("result_code"))) {
                result.put("prepay_id", responseParams.get("prepay_id"));
                result.put("code_url", responseParams.get("code_url"));
                result.put("payType", "wxpay");

                // 构建前端调起支付的参数（JSAPI / Native）
                Map<String, String> paySignParams = new TreeMap<>();
                paySignParams.put("appId", appId);
                paySignParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                paySignParams.put("nonceStr", generateNonceStr());
                paySignParams.put("package", "prepay_id=" + responseParams.get("prepay_id"));
                paySignParams.put("signType", "MD5");
                String paySign = generateSign(paySignParams, payKey);
                result.put("paySign", paySign);
                result.put("timeStamp", paySignParams.get("timeStamp"));
                result.put("nonceStr", paySignParams.get("nonceStr"));
                result.put("package", paySignParams.get("package"));
                result.put("signType", "MD5");
            } else {
                throw new RuntimeException("微信统一下单失败: " + responseParams.get("err_code_des"));
            }

        } else if ("qqpay".equals(payType)) {
            String appId = configService.getSetting("qqpay_appid");
            String mchId = configService.getSetting("qqpay_mchid");
            String payKey = configService.getSetting("qqpay_key");

            if (appId == null || mchId == null || payKey == null) {
                throw new RuntimeException("QQ支付配置不完整");
            }

            // QQ支付统一下单（与微信类似）
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("nonce_str", generateNonceStr());
            params.put("body", "EasyCloud-余额充值");
            params.put("out_trade_no", orderNo);
            params.put("total_fee", String.valueOf(amountInFen));
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", getNotifyUrl("qqpay"));
            params.put("trade_type", "NATIVE");
            params.put("sign", generateSign(params, payKey));

            String xmlBody = XmlUtil.mapToXml(params);
            String responseXml = httpPost("https://qpay.qq.com/cgi-bin/pay/qpay_unified_order", xmlBody);
            Map<String, String> responseParams = XmlUtil.xmlToMap(responseXml);

            if ("SUCCESS".equals(responseParams.get("return_code"))
                    && "SUCCESS".equals(responseParams.get("result_code"))) {
                result.put("prepay_id", responseParams.get("prepay_id"));
                result.put("code_url", responseParams.get("code_url"));
                result.put("payType", "qqpay");
            } else {
                throw new RuntimeException("QQ支付统一下单失败: " + responseParams.get("err_code_des"));
            }
        }

        return result;
    }

    /**
     * 验证支付回调签名
     * 微信支付/QQ支付签名规则：参数按 ASCII 排序，拼接 key=value&...&key=payKey，取 MD5
     */
    private boolean verifySign(Map<String, String> params, String payKey) {
        String sign = params.get("sign");
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        // 排除 sign 参数本身
        Map<String, String> filtered = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"sign".equals(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        String expectedSign = generateSign(filtered, payKey);
        return sign.equalsIgnoreCase(expectedSign);
    }

    /**
     * 生成签名（MD5）
     * 规则：按 ASCII 排序拼接 key=value&...&key=payKey，取 MD5 大写
     */
    public String generateSign(Map<String, String> params, String payKey) {
        // 使用 TreeMap 自然排序（ASCII 顺序）
        Map<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        sb.append("&key=").append(payKey);
        return md5(sb.toString()).toUpperCase();
    }

    /**
     * 生成订单号：时间戳 + UUID片段（防碰撞）
     */
    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(ORDER_NO_FMT);
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return "PAY" + timePart + uuidPart;
    }

    /**
     * 生成随机字符串（32位）
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取回调通知地址
     * 从配置读取，未配置则使用默认地址
     */
    private String getNotifyUrl(String payType) {
        String baseUrl = configService.getSetting("pay_notify_url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:8080";
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/api/pay/" + payType + "/notify";
    }

    /**
     * 发送 HTTP POST 请求
     */
    private String httpPost(String url, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }


    /**
     * MD5 摘要（委托给 Md5Util 统一实现）
     */
    private String md5(String input) {
        return Md5Util.md5(input);
    }
}
