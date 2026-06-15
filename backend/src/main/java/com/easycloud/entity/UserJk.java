package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户接口权限实体类
 * <p>
 * 对应数据库表 yixi_userjk，记录用户可访问的 API 接口权限。
 * 用于细粒度的接口访问控制，可限制特定用户只能调用部分 API。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_userjk")
public class UserJk {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 应用ID，关联 yixi_apps 表 */
    private Long appid;

    /** 允许访问的 API 接口名称 */
    private String apiName;

    /** 权限状态：0=禁用，1=启用 */
    private String status;

    /** 权限记录创建时间 */
    private LocalDateTime addtime;
}
