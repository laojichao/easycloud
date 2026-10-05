package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分记录 - 对应 PHP yixi_points
 */
@Data
@TableName("yixi_points")
public class Point {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private BigDecimal point;

    private String orderId;

    private String action;

    private LocalDateTime addtime;
}
