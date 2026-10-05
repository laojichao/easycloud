package com.easycloud.controller.admin;

import com.easycloud.common.Result;
import com.easycloud.service.TixianService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 提现审核 - 通过（登记实际到账金额）/ 拒绝（退回余额）
 */
@RestController
@RequestMapping("/api/admin/tixian")
@RequiredArgsConstructor
public class AdminTixianController {

    private final TixianService tixianService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(required = false) Integer status) {
        return Result.ok(tixianService.getAll(page, size, status));
    }

    @PostMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        BigDecimal realmoney;
        try {
            realmoney = new BigDecimal(String.valueOf(body.get("realmoney")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "请填写实际转账金额");
        }
        try {
            tixianService.approve(id, realmoney);
            return Result.ok("已通过审核");
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id) {
        try {
            tixianService.reject(id);
            return Result.ok("已拒绝，余额已退回");
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }
}
