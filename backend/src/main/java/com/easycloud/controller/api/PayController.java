package com.easycloud.controller.api;

import com.easycloud.common.ClientIpUtil;
import com.easycloud.common.Result;
import com.easycloud.common.XmlUtil;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户支付接口：下单、异步回调（XML 报文）、订单状态查询
 */
@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(String.valueOf(body.get("amount")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "金额无效");
        }
        String payType = String.valueOf(body.getOrDefault("payType", "wxpay"));
        String inviteCode = body.get("inviteCode") == null ? null : String.valueOf(body.get("inviteCode"));
        try {
            return Result.ok("下单成功", paymentService.createOrder(uid, amount, payType, inviteCode));
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PostMapping("/wxpay/notify")
    public void wxpayNotify(HttpServletRequest request, HttpServletResponse response) {
        handleNotify("wxpay", request, response);
    }

    @PostMapping("/qqpay/notify")
    public void qqpayNotify(HttpServletRequest request, HttpServletResponse response) {
        handleNotify("qqpay", request, response);
    }

    @GetMapping("/status/{orderNo}")
    public Result<?> getOrderStatus(@PathVariable String orderNo, HttpServletRequest request) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        PaymentOrder order = paymentService.getOrderStatus(orderNo);
        if (order == null || !order.getUid().equals(uid)) {
            return Result.fail(404, "订单不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("status", order.getStatus());
        data.put("amount", order.getAmount());
        return Result.ok(data);
    }

    /**
     * 异步回调统一处理：请求体为 XML 报文，应答为 XML
     */
    private void handleNotify(String payType, HttpServletRequest request, HttpServletResponse response) {
        try {
            String xmlBody = readRequestBody(request);
            Map<String, String> params = new HashMap<>(XmlUtil.xmlToMap(xmlBody));
            if (params.isEmpty()) {
                request.getParameterMap().forEach((k, v) -> params.put(k, v.length > 0 ? v[0] : ""));
            }
            Map<String, String> xmlResponse = "wxpay".equals(payType)
                    ? paymentService.wxpayNotify(params)
                    : paymentService.qqpayNotify(params);
            String xml = XmlUtil.mapToXml(xmlResponse);
            response.setContentType("text/xml;charset=UTF-8");
            response.getWriter().write(xml);
        } catch (Exception e) {
            log.error("支付回调处理异常 payType={}: {}", payType, e.getMessage());
            try {
                Map<String, String> fail = new HashMap<>();
                fail.put("status", "fail");
                response.setContentType("text/xml;charset=UTF-8");
                response.getWriter().write(XmlUtil.mapToXml(fail));
            } catch (Exception ignored) {
            }
        }
    }

    private String readRequestBody(HttpServletRequest request) {
        try {
            return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private Long getUid(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        return uid instanceof Long ? (Long) uid : null;
    }
}
