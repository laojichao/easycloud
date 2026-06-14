package com.easycloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("yixi_apps")
public class App {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appkey;

    private String name;

    private String img;

    private String note;

    private String appGg;

    private String version;

    private String versionInfo;

    @TableField("switch")
    private String switch_;

    private String ipauth;

    private String miState;

    private Integer miType;

    private String miSign;

    private String kmUnmachine;

    private String miSignIn;

    private String printSign;

    private Integer miTime;

    private String rc4Key;

    private String miRsaPrivateKey;

    private String active;

    private String logonCheckIn;

    private BigDecimal sqprice;

    private BigDecimal sqprice2;

    private BigDecimal sqprice3;

    private BigDecimal sqsprice;

    private BigDecimal sqsprice2;

    private BigDecimal cgprice;

    private String appUpdateMust;

    private String appUpdateUrl;

    private String appUpdateShow;

    private String appUpdateUrlType;

    private String lanzouPass;

    private Integer kmChangeTime;

    private Integer kmChangeNum;

    private Integer singleKmChangeNum;

    private Integer kmChange;

    private Integer longuseKmChange;

    private BigDecimal hourprice;

    private BigDecimal dayprice;

    private BigDecimal weekprice;

    private BigDecimal monthprice;

    private BigDecimal seasonprice;

    private BigDecimal yearprice;

    private BigDecimal longuseprice;

    private String total;

    private LocalDateTime date;
}
