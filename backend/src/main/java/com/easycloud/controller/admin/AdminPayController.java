package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easycloud.common.Result;
import com.easycloud.entity.PaymentOrder;
import com.easycloud.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 支付订单管理控制器
 * <p>
 * 提供管理后台对支付订单的查询和退款操作。
 * <ul>
 *   <li>GET  /api/admin/pay/orders            - 订单列表（分页+状态筛选）</li>
 *   <li>GET  /api/admin/pay/orders/{orderNo}  - 订单详情</li>
 *   <li>POST /api/admin/pay/refund/{orderNo}  - 订单退款（扣减用户余额）</li>
 * </ul>
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/pay")
@RequiredArgsConstructor
public class AdminPayController {

    private final PaymentService paymentService;

    /**
     * 订单列表（分页）
     *
     * @param page   页码（默认1）
     * @param size   每页条数（默认20）
     * @param status 状态筛选（可选：pending/paid/failed/refunded）
     */
    @GetMapping("/orders")
    public Result<?> getOrderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        if (page < 1) page = 1;
        if (size < 1) size = 1;
        if (size > 500) size = 500;
        IPage<PaymentOrder> orderPage = paymentService.getAllOrderList(page, size, status);
        return Result.ok(orderPage);
    }

    /**
     * 订单详情
     */
    @GetMapping("/orders/{orderNo}")
    public Result<?> getOrderDetail(@PathVariable String orderNo) {
        PaymentOrder order = paymentService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.ok(order);
    }

    /**
     * 退款
     */
    @PostMapping("/refund/{orderNo}")
    public Result<?> refundOrder(@PathVariable String orderNo) {
        try {
            paymentService.refundOrder(orderNo);
            return Result.ok("退款成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
