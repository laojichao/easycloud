package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用-用户授权关联 - 对应 PHP yixi_appuser
 */
@Data
@TableName("yixi_appuser")
public class AppUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long appid;

    private Long uid;

    private String status;

    private LocalDateTime addtime;
}
