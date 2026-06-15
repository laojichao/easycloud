package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.InviteLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邀请返利记录 Mapper
 * 对应实体 {@link InviteLog}，表 yixi_invitelog
 * 提供邀请返利记录的 CRUD 操作
 */
@Mapper
public interface InviteLogMapper extends BaseMapper<InviteLog> {
}
