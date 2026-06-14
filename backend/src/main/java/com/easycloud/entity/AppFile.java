package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yixi_appfile")
public class AppFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long uid;

    private Long appid;

    private String type;

    private String fileUrl;

    private String lanzouPass;

    private LocalDateTime addtime;

    private String state;

    private String note;
}
