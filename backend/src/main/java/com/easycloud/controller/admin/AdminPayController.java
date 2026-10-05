package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easycloud.common.Result;
import com.easycloud.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 支付订单管理 - 列表/详情/退款
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/pay")
@RequiredArgsConstructor
public class AdminPayController {

    private final PaymentService paymentService;

    @GetMapping("/orders")
    public Result<?> getOrderList(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "20") int size,
                                  @RequestParam(required = false) String status) {
        IPage<?> result = paymentService.getAllOrderList(page, size, status);
        return Result.ok(result);
    }

    @GetMapping("/orders/{orderNo}")
    public Result<?> getOrderDetail(@PathVariable String orderNo) {
        Object order = paymentService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.ok(order);
    }

    @PostMapping("/refund/{orderNo}")
    public Result<?> refundOrder(@PathVariable String orderNo) {
        try {
            if (paymentService.refundOrder(orderNo)) {
                return Result.ok("退款成功");
            }
            return Result.fail(400, "退款失败");
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }
}
