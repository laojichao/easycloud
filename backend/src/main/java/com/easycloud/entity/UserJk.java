package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户接口监控关联 - 对应 PHP yixi_userjk
 */
@Data
@TableName("yixi_userjk")
public class UserJk {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private Long appid;

    private String apiName;

    private String status;

    private LocalDateTime addtime;
}
