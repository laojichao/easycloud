package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分/佣金记录实体类
 * <p>
 * 对应数据库表 yixi_points，记录用户的积分变动明细。
 * 积分来源包括：签到奖励、邀请返利、消费返佣、管理员操作等。
 * 可用于兑换余额或提现。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_points")
public class Point {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 积分变动值（正数为增加，负数为扣除） */
    private BigDecimal point;

    /** 关联的订单号（如支付订单、卡密订单） */
    private String orderId;

    /** 积分变动原因/动作类型，如 "checkin"、"invite"、"consume" */
    private String action;

    /** 记录创建时间 */
    private LocalDateTime addtime;
}
