package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 邀请返利记录 - 对应 PHP yixi_invitelog
 */
@Data
@TableName("yixi_invitelog")
public class InviteLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String qq;

    private String type;

    private BigDecimal money;

    private String bz;

    private LocalDateTime creationTime;
}
