package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单 Mapper
 * 对应实体 {@link WorkOrder}，表 yixi_workorder
 * 提供用户工单的 CRUD 操作
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
}
