package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单
 */
@Data
@TableName("yixi_payment_order")
public class PaymentOrder {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_REFUNDED = "refunded";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long uid;

    private BigDecimal amount;

    /** 支付方式：wxpay / qqpay */
    private String payType;

    private String status;

    private String tradeNo;

    private String inviteCode;

    private LocalDateTime createTime;

    private LocalDateTime payTime;
}
