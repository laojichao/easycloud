package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统配置实体类
 * <p>
 * 对应数据库表 yixi_config，采用键值对（K-V）方式存储系统配置项。
 * 包含站点名称、注册开关、支付配置、邮件/短信设置等全局参数。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_config")
public class SysConfig {

    /** 配置项键名（主键），如 "sitename"、"reg_switch" */
    @TableId
    private String k;

    /** 配置项值 */
    private String v;
}
