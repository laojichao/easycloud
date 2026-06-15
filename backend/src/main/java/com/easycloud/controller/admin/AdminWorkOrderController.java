package com.easycloud.controller.admin;

import com.easycloud.common.Result;
import com.easycloud.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员工单管理控制器
 * <p>
 * 提供管理后台对用户工单的查看、回复和关闭功能。
 * 工单状态流转：待处理(0) → 已处理(1)（回复后）→ 已关闭(2)
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/workorders")
@RequiredArgsConstructor
public class AdminWorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 工单列表（支持状态筛选）
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status) {
        return Result.ok(workOrderService.getAll(page, size, status));
    }

    /**
     * 回复工单
     */
    @PostMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        if (reply == null || reply.isEmpty()) {
            return Result.fail("回复内容不能为空");
        }
        try {
            workOrderService.reply(id, reply);
            return Result.ok("回复成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 关闭工单
     */
    @PostMapping("/{id}/close")
    public Result<?> close(@PathVariable Long id) {
        try {
            workOrderService.close(id);
            return Result.ok("关闭成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
