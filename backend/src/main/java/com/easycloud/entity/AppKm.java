package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yixi_appkm")
public class AppKm {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long upid;

    private Long appid;

    private String kami;

    private String type;

    private String note;

    private Integer amount;

    private String user;

    private Long useTime;

    private String endTime;

    private LocalDateTime addtime;

    private String kmUse;

    private Integer kmChange;

    private String kmTime;

    private String userIp;

    private Long kmChangeTime;

    private String state;
}
