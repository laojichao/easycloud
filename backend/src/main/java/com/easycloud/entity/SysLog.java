package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * <p>
 * 对应数据库表 yixi_log，记录系统操作日志。
 * 包括用户登录、卡密操作、支付行为等各类事件的审计追踪。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_log")
public class SysLog {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作用户ID（字符串类型，兼容匿名用户如 "0"） */
    private String uid;

    /**
     * 日志类型/操作分类
     * 如 "login"、"reg"、"usekm"、"pay" 等
     */
    private String type;

    /** 日志详细数据/操作描述 */
    private String data;

    /** 操作者IP地址 */
    private String ip;

    /** 操作者IP归属地城市 */
    private String city;

    /** 日志记录时间 */
    private LocalDateTime date;
}
