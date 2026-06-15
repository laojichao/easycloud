package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 对应数据库表 yixi_user，存储平台注册用户的基本信息。
 * 用户可通过用户名密码登录，或使用卡密激活应用。
 * 包含账户余额（rmb），可用于购买卡密或提现。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_user")
public class User {

    /** 用户ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long uid;

    /** 用户名（登录账号，唯一） */
    private String user;

    /** 登录密码（BCrypt 加密存储） */
    private String pwd;

    /** 账户余额（人民币，单位：元） */
    private BigDecimal rmb;

    /** QQ 号码（用于联系和通知） */
    private String qq;

    /** 电子邮箱（用于找回密码和通知） */
    private String email;

    /** 注册时的IP地址 */
    private String ip;

    /** 注册时间 */
    private LocalDateTime regdate;
}
