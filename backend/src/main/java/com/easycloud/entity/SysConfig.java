package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yixi_config")
public class SysConfig {

    @TableId
    private String k;

    private String v;
}
