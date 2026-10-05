package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 - 对应 PHP yixi_user
 */
@Data
@TableName("yixi_user")
public class User {

    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    /** 用户名（列名 user 为数据库保留字，需转义） */
    @TableField("\u0060user\u0060")
    private String user;

    private String pwd;

    private BigDecimal rmb;

    private String qq;

    private String email;

    private String ip;

    private String invitecode;

    private LocalDateTime regdate;
}
