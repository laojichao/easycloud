package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.WorkOrder;
import com.easycloud.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 工单管理
 */
@RestController
@RequestMapping("/api/admin/workorders")
@RequiredArgsConstructor
public class AdminWorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(required = false) Integer status) {
        Page<WorkOrder> result = workOrderService.getAll(page, size, status);
        return Result.ok(result);
    }

    @PostMapping("/{id}/reply")
    public Result<?> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            workOrderService.reply(id, body.get("reply"));
            return Result.ok("回复成功");
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PostMapping("/{id}/close")
    public Result<?> close(@PathVariable Long id) {
        try {
            workOrderService.close(id);
            return Result.ok("工单已关闭");
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }
}
