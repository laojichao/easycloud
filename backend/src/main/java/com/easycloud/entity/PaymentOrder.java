package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 * <p>
 * 对应数据库表 yixi_payment_order，存储用户充值/购买的支付订单信息。
 * 支持微信支付（wxpay）和QQ支付（qqpay）两种渠道。
 * 订单状态机：pending -> paid / failed / refunded
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_payment_order")
public class PaymentOrder {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号（唯一，系统自动生成的唯一标识） */
    private String orderNo;

    /** 下单用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 订单金额（人民币，单位：元） */
    private BigDecimal amount;

    /**
     * 支付渠道类型
     * wxpay = 微信支付
     * qqpay = QQ钱包支付
     */
    private String payType;

    /**
     * 订单状态
     * pending = 待支付
     * paid = 已支付
     * failed = 支付失败
     * refunded = 已退款
     */
    private String status;

    /** 第三方支付平台返回的交易流水号 */
    private String tradeNo;

    /** 订单创建时间 */
    private LocalDateTime createTime;

    /** 支付完成时间（未支付时为 null） */
    private LocalDateTime payTime;
}
