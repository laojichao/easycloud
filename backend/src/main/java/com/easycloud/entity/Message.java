package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台消息实体类
 * <p>
 * 对应数据库表 yixi_message，存储平台发布的公告/消息。
 * 支持不同类型的消息分类，用于向用户推送系统通知、活动信息等。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_message")
public class Message {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 消息标题 */
    private String title;

    /** 消息类型，如 "notice"、"activity"、"system" */
    private String type;

    /** 消息正文内容 */
    private String content;

    /** 消息发布时间 */
    private LocalDateTime addtime;
}
