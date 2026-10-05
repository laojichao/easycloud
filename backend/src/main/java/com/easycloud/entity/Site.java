package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分站 - 对应 PHP yixi_site
 */
@Data
@TableName("yixi_site")
public class Site {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private String domain;

    private String sitename;

    private LocalDateTime endtime;

    private String status;

    private LocalDateTime addtime;
}
