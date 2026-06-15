package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡密实体类
 * <p>
 * 对应数据库表 yixi_appkm，存储应用关联的卡密信息。
 * 每张卡密属于一个应用（App），具有类型、时长、使用状态等属性。
 * 是用户激活软件的核心凭证。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_appkm")
public class AppKm {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 上级/操作者用户ID，标识该卡密由谁生成或分配 */
    private Long upid;

    /** 所属应用ID，关联 yixi_apps 表 */
    private Long appid;

    /** 卡密字符串，用户用于激活的凭证 */
    private String kami;

    /**
     * 卡密类型
     * 对应 App 表的时长配置，如：hour/week/month/season/year/longuse
     */
    private String type;

    /** 卡密备注信息 */
    private String note;

    /** 卡密面值/金额 */
    private Integer amount;

    /** 使用该卡密的用户名 */
    private String user;

    /** 卡密激活使用时间（时间戳，秒） */
    private Long useTime;

    /** 卡密到期时间（字符串格式的时间戳或日期） */
    private String endTime;

    /** 卡密生成/添加时间 */
    private LocalDateTime addtime;

    /** 卡密使用状态标记 */
    private String kmUse;

    /** 卡密已换绑次数 */
    private Integer kmChange;

    /** 卡密有效时长（如 "7d" 表示 7 天） */
    private String kmTime;

    /** 使用该卡密的用户IP地址 */
    private String userIp;

    /** 最近一次换绑操作时间（时间戳，秒） */
    private Long kmChangeTime;

    /**
     * 卡密状态
     * 0=未使用, 1=已使用, 2=已禁用
     */
    private String state;
}
