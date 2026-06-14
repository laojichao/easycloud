package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yixi_log")
public class SysLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uid;

    private String type;

    private String data;

    private String ip;

    private String city;

    private LocalDateTime date;
}
