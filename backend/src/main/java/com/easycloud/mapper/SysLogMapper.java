package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 * 对应实体 {@link SysLog}，表 yixi_log
 * 提供操作审计日志的 CRUD 操作
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}
