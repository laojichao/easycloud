package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 程序/产品实体
 * 对应数据库表 yixi_program
 * 管理平台上的软件产品信息，包含版本和下载地址
 */
@Data
@TableName("yixi_program")
public class Program {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String version;
    private String downloadUrl;
    private String status;
    private LocalDateTime addtime;
}
