package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 平台消息 Mapper
 * 对应实体 {@link Message}，表 yixi_message
 * 提供管理员发布平台通知消息的 CRUD 操作
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
