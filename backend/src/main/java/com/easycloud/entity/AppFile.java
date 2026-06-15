package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用文件实体类
 * <p>
 * 对应数据库表 yixi_appfile，存储应用关联的下载文件信息。
 * 支持直链和蓝奏云网盘链接两种文件托管方式。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_appfile")
public class AppFile {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 上传者用户ID */
    private Long uid;

    /** 所属应用ID，关联 yixi_apps 表 */
    private Long appid;

    /** 文件类型，如 apk、zip 等 */
    private String type;

    /** 文件下载地址（直链或蓝奏云链接） */
    private String fileUrl;

    /** 蓝奏云提取密码（当文件托管在蓝奏云时使用） */
    private String lanzouPass;

    /** 文件上传/添加时间 */
    private LocalDateTime addtime;

    /** 文件状态：0=禁用，1=启用 */
    private String state;

    /** 文件备注说明 */
    private String note;
}
