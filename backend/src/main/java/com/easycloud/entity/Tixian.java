package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录实体类
 * <p>
 * 对应数据库表 yixi_tixian，存储用户提现申请及处理记录。
 * 用户可将账户余额提现到支付宝/微信/QQ钱包等收款账户。
 * 提现申请需管理员审核后方可到账。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_tixian")
public class Tixian {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请提现的用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 收款账号（支付宝账号、微信号等） */
    private String account;

    /** 收款人姓名 */
    private String name;

    /** 提现申请金额（人民币，单位：元） */
    private BigDecimal money;

    /** 实际到账金额（扣除手续费后的金额） */
    private BigDecimal realmoney;

    /**
     * 提现状态
     * 0=待处理（用户提交后默认状态）
     * 1=已处理（管理员已审核通过并打款）
     * 2=已拒绝（管理员审核不通过）
     */
    private Integer status;

    /** 提现方式，如 "alipay"、"wxpay"、"qqpay" */
    private String type;

    /** 提现申请提交时间 */
    private LocalDateTime addtime;

    /** 提现处理完成时间 */
    private LocalDateTime endtime;
}
