package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录 - 对应 PHP yixi_qiandao
 */
@Data
@TableName("yixi_qiandao")
public class Checkin {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private LocalDate date;

    private BigDecimal reward;

    private LocalDateTime addtime;
}
