package com.easycloud.controller.api;

import com.easycloud.common.Result;
import com.easycloud.common.XmlUtil;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 支付接口控制器
 * <p>
 * 处理用户充值/购买的支付流程和支付网关回调。
 * 支持微信支付和QQ支付两种渠道。
 * <ul>
 *   <li>POST /api/pay/create        - 创建支付订单（需用户认证）</li>
 *   <li>POST /api/pay/wxpay/notify   - 微信支付异步回调（无需认证，支付网关调用）</li>
 *   <li>POST /api/pay/qqpay/notify   - QQ支付异步回调（无需认证，支付网关调用）</li>
 *   <li>GET  /api/pay/status/{orderNo} - 查询订单状态</li>
 * </ul>
 * <p>
 * 对应原 PHP 项目的支付模块
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     * 前端传入：amount(元), payType(wxpay/qqpay)
     * uid 从请求属性中获取（由 UserAuthInterceptor 注入）
     */
    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody Map<String, Object> body,
                                 HttpServletRequest request) {
        try {
            // 从请求属性获取 uid（UserAuthInterceptor 注入）
            Long uid = (Long) request.getAttribute("uid");
            if (uid == null) {
                return Result.fail(401, "未登录");
            }
            if (body.get("amount") == null || body.get("payType") == null) {
                return Result.fail("充值金额和支付方式不能为空");
            }
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String payType = body.get("payType").toString();

            Map<String, Object> payParams = paymentService.createOrder(uid, amount, payType);
            return Result.ok(payParams);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            return Result.fail("创建支付订单失败，请稍后重试");
        }
    }

    /**
     * 微信支付回调 - 无需 JWT 认证（支付网关调用）
     */
    @PostMapping("/wxpay/notify")
    public void wxpayNotify(HttpServletRequest request, HttpServletResponse response) {
        handleNotify("wxpay", request, response);
    }

    /**
     * QQ支付回调 - 无需 JWT 认证（支付网关调用）
     */
    @PostMapping("/qqpay/notify")
    public void qqpayNotify(HttpServletRequest request, HttpServletResponse response) {
        handleNotify("qqpay", request, response);
    }

    /**
     * 统一回调处理
     */
    private void handleNotify(String payType, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 读取请求体（XML 格式）
            String xmlBody = readRequestBody(request);
            log.info("收到支付回调: payType={}, body={}", payType, xmlBody);

            // 解析 XML 为 Map
            Map<String, String> params = XmlUtil.xmlToMap(xmlBody);

            // 调用 PaymentService 处理回调
            Map<String, String> result;
            if ("wxpay".equals(payType)) {
                result = paymentService.wxpayNotify(params);
            } else {
                result = paymentService.qqpayNotify(params);
            }

            // 返回 XML 响应（微信/QQ支付要求的格式）
            String xmlResponse = XmlUtil.mapToXml(result);
            response.setContentType("application/xml;charset=UTF-8");
            response.getWriter().write(xmlResponse);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("处理支付回调异常: payType={}", payType, e);
            try {
                Map<String, String> errorResult = new HashMap<>();
                errorResult.put("return_code", "FAIL");
                errorResult.put("return_msg", "系统异常");
                response.setContentType("application/xml;charset=UTF-8");
                response.getWriter().write(XmlUtil.mapToXml(errorResult));
                response.getWriter().flush();
            } catch (Exception ex) {
                log.error("返回错误响应失败", ex);
            }
        }
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/status/{orderNo}")
    public Result<?> getOrderStatus(@PathVariable String orderNo) {
        PaymentOrder order = paymentService.getOrderStatus(orderNo);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("status", order.getStatus());
        data.put("amount", order.getAmount());
        data.put("payType", order.getPayType());
        data.put("createTime", order.getCreateTime());
        data.put("payTime", order.getPayTime());
        return Result.ok(data);
    }

    /**
     * 读取请求体
     */
    private String readRequestBody(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("读取请求体失败", e);
            return "";
        }
    }

}
