package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yixi_sig")
public class Sig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long upid;

    private Long appid;

    private String token;

    private String appsign;

    private String vpn;

    private Integer vpntype;

    private Integer safetype;

    private LocalDateTime addtime;
}
