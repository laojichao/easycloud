package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 应用用户关联实体类
 * <p>
 * 对应数据库表 yixi_appuser，记录用户与应用之间的绑定关系。
 * 当用户使用卡密激活某应用后，会在此表中建立关联记录。
 * 用于查询某用户拥有哪些应用的授权，或某应用有哪些授权用户。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_appuser")
public class AppUser {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用ID，关联 yixi_apps 表 */
    private Long appid;

    /** 用户ID，关联 yixi_user 表 */
    private Long uid;

    /** 绑定状态：0=禁用，1=正常 */
    private String status;

    /** 绑定/授权时间 */
    private LocalDateTime addtime;
}
