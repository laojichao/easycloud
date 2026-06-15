package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录实体类
 * <p>
 * 对应数据库表 yixi_qiandao，记录用户每日签到信息。
 * 每个用户每天只能签到一次，签到可获得积分或余额奖励。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_qiandao")
public class Checkin {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 签到用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 签到日期（用于判断当日是否已签到） */
    private LocalDate date;

    /** 签到奖励金额（人民币，单位：元） */
    private BigDecimal reward;

    /** 签到记录创建时间 */
    private LocalDateTime addtime;
}
