package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应用实体类
 * <p>
 * 对应数据库表 yixi_apps，存储应用的基本信息、加密配置、定价策略和版本更新等。
 * 是整个卡密系统的核心业务实体，一个 App 可关联多个卡密（AppKm）和文件（AppFile）。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Data
@TableName("yixi_apps")
public class App {

    /** 主键ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用唯一标识密钥，用于 API 调用鉴权 */
    private String appkey;

    /** 应用名称 */
    private String name;

    /** 应用图标 URL */
    private String img;

    /** 应用备注说明 */
    private String note;

    /** 应用公告内容，客户端可拉取展示 */
    private String appGg;

    /** 当前应用版本号 */
    private String version;

    /** 版本更新说明 */
    private String versionInfo;

    /**
     * 应用总开关
     * 对应数据库字段 switch（Java 保留字，使用下划线后缀避免冲突）
     */
    @TableField("switch")
    @JsonProperty("switch")
    private String switch_;

    /** IP 绑定验证开关：1=开启，0=关闭 */
    private String ipauth;

    /** 加密通信开关：1=开启，0=关闭 */
    private String miState;

    /**
     * 加密类型
     * 0=明文, 1=自定义RC4, 2=Base64, 3=标准RC4, 4=AES-128-CBC, 5=Base64
     */
    private Integer miType;

    /** 加密签名密钥 */
    private String miSign;

    /** 卡密解绑/换机功能开关：1=允许解绑，0=不允许 */
    private String kmUnmachine;

    /** 登录签到功能开关：1=开启，0=关闭 */
    private String miSignIn;

    /** 打印签名标识 */
    private String printSign;

    /** 时间校验偏移量（秒），用于防篡改校验 */
    private Integer miTime;

    /** RC4 加密密钥（当 miType 为 1 或 3 时使用） */
    private String rc4Key;

    /** RSA 私钥（用于非对称加密验签） */
    private String miRsaPrivateKey;

    /** 激活验证开关：1=需要激活，0=不需要 */
    private String active;

    /** 登录签到校验开关：1=开启，0=关闭 */
    private String logonCheckIn;

    /** 授权单价（人民币，元） */
    private BigDecimal sqprice;

    /** 授权单价2（第二档价格） */
    private BigDecimal sqprice2;

    /** 授权单价3（第三档价格） */
    private BigDecimal sqprice3;

    /** 授权试用单价 */
    private BigDecimal sqsprice;

    /** 授权试用单价2 */
    private BigDecimal sqsprice2;

    /** 超管价格 */
    private BigDecimal cgprice;

    /** 强制更新开关：1=强制更新，0=不强制 */
    private String appUpdateMust;

    /** 应用更新下载地址 */
    private String appUpdateUrl;

    /** 更新提示内容 */
    private String appUpdateShow;

    /** 更新链接类型：0=直链，1=蓝奏云 */
    private String appUpdateUrlType;

    /** 蓝奏云分享链接提取密码 */
    private String lanzouPass;

    /** 卡密换绑时间限制（单位由系统配置决定，通常为小时） */
    private Integer kmChangeTime;

    /** 卡密换绑总次数限制 */
    private Integer kmChangeNum;

    /** 单张卡密换绑次数限制 */
    private Integer singleKmChangeNum;

    /** 卡密换绑功能开关：1=允许，0=不允许 */
    private Integer kmChange;

    /** 长期卡换绑开关：1=允许长期卡换绑，0=不允许 */
    private Integer longuseKmChange;

    /** 按小时计费价格 */
    private BigDecimal hourprice;

    /** 按天计费价格 */
    private BigDecimal dayprice;

    /** 按周计费价格 */
    private BigDecimal weekprice;

    /** 按月计费价格 */
    private BigDecimal monthprice;

    /** 按季计费价格 */
    private BigDecimal seasonprice;

    /** 按年计费价格 */
    private BigDecimal yearprice;

    /** 长期使用价格（一次购买永久使用） */
    private BigDecimal longuseprice;

    /** 应用总使用量/总卡密数 */
    private String total;

    /** 创建/更新时间 */
    private LocalDateTime date;
}
