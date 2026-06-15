package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单记录实体类
 * <p>
 * 对应数据库表 yixi_workorder，存储用户提交的工单及管理员的回复。
 * 工单系统用于用户与管理员之间的沟通，支持状态流转（待处理 -> 已处理 -> 已关闭）。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_workorder")
public class WorkOrder {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 提交工单的用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 工单标题/主题 */
    private String title;

    /** 工单详细内容/描述 */
    private String content;

    /** 管理员回复内容 */
    private String reply;

    /**
     * 工单状态
     * 0=待处理（用户提交后默认状态）
     * 1=已处理（管理员已回复）
     * 2=已关闭（工单完结）
     */
    private Integer status;

    /** 工单提交时间 */
    private LocalDateTime addtime;

    /** 管理员最近回复时间 */
    private LocalDateTime replytime;
}
