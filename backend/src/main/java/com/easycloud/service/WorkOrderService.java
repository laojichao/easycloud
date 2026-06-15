package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.entity.WorkOrder;
import com.easycloud.mapper.WorkOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 工单服务
 * <p>
 * 处理用户工单的创建、查询、回复和关闭等业务逻辑。
 * 对应原 PHP 项目 yixi_workorder 相关逻辑。
 * <p>
 * 工单状态流转：待处理(0) → 已处理(1) → 已关闭(2)
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    /**
     * 创建工单
     *
     * @param uid     用户ID
     * @param title   工单标题
     * @param content 工单内容
     * @return 创建的工单
     */
    @Transactional
    public WorkOrder create(Long uid, String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new RuntimeException("工单标题不能为空");
        }
        if (!StringUtils.hasText(content)) {
            throw new RuntimeException("工单内容不能为空");
        }

        WorkOrder order = new WorkOrder();
        order.setUid(uid);
        order.setTitle(title);
        order.setContent(content);
        order.setStatus(0); // 待处理
        order.setAddtime(LocalDateTime.now());
        workOrderMapper.insert(order);
        return order;
    }

    /**
     * 获取用户的工单列表
     */
    public Page<WorkOrder> getList(Long uid, int page, int size) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getUid, uid)
                .orderByDesc(WorkOrder::getAddtime);
        return workOrderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 获取所有工单列表（管理员）
     *
     * @param page   页码
     * @param size   每页条数
     * @param status 状态筛选，null 表示全部
     */
    public Page<WorkOrder> getAll(int page, int size, Integer status) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        wrapper.orderByDesc(WorkOrder::getAddtime);
        return workOrderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 管理员回复工单
     *
     * @param id    工单ID
     * @param reply 回复内容
     */
    @Transactional
    public void reply(Long id, String reply) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setReply(reply);
        order.setStatus(1); // 已处理
        order.setReplytime(LocalDateTime.now());
        workOrderMapper.updateById(order);
    }

    /**
     * 关闭工单
     *
     * @param id 工单ID
     */
    @Transactional
    public void close(Long id) {
        WorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("工单不存在");
        }
        order.setStatus(2); // 已关闭
        workOrderMapper.updateById(order);
    }
}
