package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 站点/分站实体类
 * <p>
 * 对应数据库表 yixi_site，存储分站（子站点）信息。
 * 分站功能允许用户创建独立运营的子平台，拥有独立域名和品牌。
 * 分站有有效期限制，到期后需续费。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_site")
public class Site {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分站所属用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 分站绑定的域名 */
    private String domain;

    /** 分站名称/品牌名 */
    private String sitename;

    /** 分站到期时间（到期后将无法访问） */
    private LocalDateTime endtime;

    /** 分站状态：0=禁用，1=正常 */
    private String status;

    /** 分站创建时间 */
    private LocalDateTime addtime;
}
