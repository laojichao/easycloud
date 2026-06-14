package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yixi_cache")
public class SysCache {

    @TableId
    private String k;

    private String v;
}
