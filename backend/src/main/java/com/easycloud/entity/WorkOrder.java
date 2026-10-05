package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单 - 对应 PHP yixi_workorder（status: 0=待处理 1=已处理 2=已关闭）
 */
@Data
@TableName("yixi_workorder")
public class WorkOrder {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_REPLIED = 1;
    public static final int STATUS_CLOSED = 2;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    private LocalDateTime addtime;

    private LocalDateTime replytime;
}
