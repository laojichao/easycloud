package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台消息 - 对应 PHP yixi_message
 */
@Data
@TableName("yixi_message")
public class Message {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String type;

    private String content;

    private LocalDateTime addtime;
}
