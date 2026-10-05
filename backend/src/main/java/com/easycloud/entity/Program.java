package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 程序/产品 - 对应 PHP yixi_program
 */
@Data
@TableName("yixi_program")
public class Program {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String version;

    private String downloadUrl;

    private String status;

    private LocalDateTime addtime;
}
