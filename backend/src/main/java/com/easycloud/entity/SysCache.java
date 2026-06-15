package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统缓存实体类
 * <p>
 * 对应数据库表 yixi_cache，采用键值对（K-V）方式存储系统级缓存数据。
 * 用于缓存高频访问的配置或计算结果，减少数据库查询压力。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_cache")
public class SysCache {

    /** 缓存键名（主键） */
    @TableId
    private String k;

    /** 缓存值 */
    private String v;
}
