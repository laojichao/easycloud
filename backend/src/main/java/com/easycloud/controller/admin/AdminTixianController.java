package com.easycloud.controller.admin;

import com.easycloud.common.Result;
import com.easycloud.service.TixianService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 管理员提现管理控制器
 * <p>
 * 提供管理后台对用户提现申请的审核操作，包括批准（设置实际到账金额）和拒绝（退回用户余额）。
 * 提现状态：待处理(0) → 已处理(1) 或 已拒绝(2)
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/tixian")
@RequiredArgsConstructor
public class AdminTixianController {

    private final TixianService tixianService;

    /**
     * 提现列表（支持状态筛选）
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status) {
        return Result.ok(tixianService.getAll(page, size, status));
    }

    /**
     * 批准提现
     */
    @PostMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            BigDecimal realmoney = new BigDecimal(body.get("realmoney").toString());
            tixianService.approve(id, realmoney);
            return Result.ok("批准成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 拒绝提现
     */
    @PostMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id) {
        try {
            tixianService.reject(id);
            return Result.ok("拒绝成功，余额已退回");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
