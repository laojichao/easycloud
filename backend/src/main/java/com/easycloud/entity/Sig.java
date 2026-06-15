package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户签名/会话实体类
 * <p>
 * 对应数据库表 yixi_sig，存储用户登录后的会话签名信息。
 * 用于 API 接口的身份校验和安全验证。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_sig")
public class Sig {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户ID，关联 yixi_user 表 */
    private Long upid;

    /** 所属应用ID，关联 yixi_apps 表 */
    private Long appid;

    /** 用户登录 Token，用于接口鉴权 */
    private String token;

    /** 应用签名标识 */
    private String appsign;

    /** VPN 连接标识 */
    private String vpn;

    /** VPN 连接类型 */
    private Integer vpntype;

    /** 安全验证类型 */
    private Integer safetype;

    /** 签名/会话创建时间 */
    private LocalDateTime addtime;
}
