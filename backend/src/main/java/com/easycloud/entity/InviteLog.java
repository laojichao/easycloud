package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 邀请返利记录实体类
 * <p>
 * 对应数据库表 yixi_invitelog，记录用户邀请好友注册或消费后的返利明细。
 * 邀请机制：用户A邀请用户B注册或消费后，A 可获得一定比例的返利奖励。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_invitelog")
public class InviteLog {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 获得返利的用户ID（邀请人），关联 yixi_user 表 */
    private Long uid;

    /** 被邀请人的QQ号 */
    private String qq;

    /** 返利类型 */
    private String type;

    /** 返利金额（人民币，单位：元） */
    private BigDecimal money;

    /** 备注说明（bz = 备注的拼音缩写） */
    private String bz;

    /** 返利记录创建时间 */
    private LocalDateTime creationTime;
}
