package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现记录 - 对应 PHP yixi_tixian（status: 0=待处理 1=已通过 2=已拒绝）
 */
@Data
@TableName("yixi_tixian")
public class Tixian {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String account;

    private String name;

    private BigDecimal money;

    private BigDecimal realmoney;

    private Integer status;

    private String type;

    private LocalDateTime addtime;

    private LocalDateTime endtime;
}
