package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.WorkOrder;
import com.easycloud.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 工单服务 - 对应 PHP yixi_workorder
 */
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    public WorkOrder create(Long uid, String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new RuntimeException("请输入工单标题");
        }
        WorkOrder order = new WorkOrder();
        order.setUid(uid);
        order.setTitle(title);
        order.setContent(content);
        order.setStatus(WorkOrder.STATUS_PENDING);
        order.setAddtime(LocalDateTime.now());
        workOrderMapper.insert(order);
        return order;
    }

    public Page<WorkOrder> getList(Long uid, int page, int size) {
        return workOrderMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<WorkOrder>()
                        .eq(WorkOrder::getUid, uid)
                        .orderByDesc(WorkOrder::getAddtime));
    }

    public Page<WorkOrder> getAll(int page, int size, Integer status) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        wrapper.orderByDesc(WorkOrder::getAddtime);
        return workOrderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public void reply(Long id, String reply) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setReply(reply);
        order.setReplytime(LocalDateTime.now());
        order.setStatus(WorkOrder.STATUS_REPLIED);
        workOrderMapper.updateById(order);
    }

    public void close(Long id) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(WorkOrder.STATUS_CLOSED);
        workOrderMapper.updateById(order);
    }
}
