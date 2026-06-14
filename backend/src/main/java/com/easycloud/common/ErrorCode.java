package com.easycloud.common;

import java.util.Map;

/**
 * 错误码注册表 - 精确复刻 PHP includes/msg.php
 * 所有 API 错误码的集中管理
 */
public class ErrorCode {

    private static final Map<Integer, String> MESSAGES = Map.ofEntries(
            Map.entry(200, "success"),
            Map.entry(201, "failure"),
            Map.entry(100, "请绑定应用ID"),
            Map.entry(101, "应用不存在"),
            Map.entry(102, "应用已关闭"),
            Map.entry(103, "登录已关闭"),
            Map.entry(104, "签名为空"),
            Map.entry(105, "数据过期"),
            Map.entry(106, "签名有误"),
            Map.entry(107, "数据为空"),
            Map.entry(108, "未发现时间变量"),
            Map.entry(110, "请填写账号"),
            Map.entry(111, "请填写密码"),
            Map.entry(112, "请填写机器码"),
            Map.entry(113, "账号/密码不正确"),
            Map.entry(114, "账号已禁用"),
            Map.entry(115, "账号已存在"),
            Map.entry(116, "账号不可用"),
            Map.entry(117, "注册频率过快"),
            Map.entry(118, "邀请人不存在"),
            Map.entry(119, "密码不可用"),
            Map.entry(120, "验证码为空"),
            Map.entry(121, "管理员未开启邮箱验证"),
            Map.entry(122, "账号不存在"),
            Map.entry(123, "验证码发送频率过快"),
            Map.entry(124, "验证码不正确"),
            Map.entry(125, "TOKEN为空"),
            Map.entry(126, "TOKEN不可用"),
            Map.entry(127, "TOKEN不存在"),
            Map.entry(128, "已设置，不可更改"),
            Map.entry(129, "名称为空"),
            Map.entry(130, "订单号为空"),
            Map.entry(131, "请选择支付方式"),
            Map.entry(132, "请选择商品"),
            Map.entry(133, "应用未开启支付"),
            Map.entry(134, "请先设置异步通知地址"),
            Map.entry(135, "不支持该支付方式"),
            Map.entry(136, "商品不存在"),
            Map.entry(137, "创建订单失败"),
            Map.entry(138, "支付错误"),
            Map.entry(139, "支付未知错误"),
            Map.entry(140, "请填写订单信息"),
            Map.entry(141, "提交方式不正确"),
            Map.entry(142, "不支持上传类型"),
            Map.entry(143, "积分ID为空"),
            Map.entry(144, "积分活动不存在"),
            Map.entry(145, "积分活动已关闭"),
            Map.entry(146, "未开启签到功能"),
            Map.entry(147, "今天已签到"),
            Map.entry(148, "卡密不能为空"),
            Map.entry(149, "卡密不存在"),
            Map.entry(150, "卡密已被使用"),
            Map.entry(151, "卡密已禁用"),
            Map.entry(152, "卡密类型不匹配"),
            Map.entry(153, "订单不存在"),
            Map.entry(154, "等待支付"),
            Map.entry(155, "未知订单状态"),
            Map.entry(156, "请输入openid"),
            Map.entry(157, "请输入access_token"),
            Map.entry(158, "身份信息不正确"),
            Map.entry(159, "微信openid不正确"),
            Map.entry(160, "微信已绑定其他账号"),
            Map.entry(161, "请输入QQ互联ID"),
            Map.entry(162, "未知登录错误"),
            Map.entry(163, "应用不允许该登录方式"),
            Map.entry(164, "应用不允许当前操作"),
            Map.entry(165, "账号未绑定邮箱"),
            Map.entry(166, "卡密只能充值到一个账号"),
            Map.entry(167, "不支持积分卡登录"),
            Map.entry(168, "订单已存在"),
            Map.entry(169, "当前IP不匹配"),
            Map.entry(170, "接口已关闭"),
            Map.entry(171, "接口维护中"),
            Map.entry(172, "接口未添加或未购买"),
            Map.entry(173, "接口已过期"),
            Map.entry(199, "您已是永久会员"),
            Map.entry(400, "无相关操作"),
            Map.entry(401, "数据不正确")
    );

    /**
     * 根据错误码获取消息
     *
     * @param code 错误码
     * @return 对应的消息，如果码不存在返回 "未知错误"
     */
    public static String getMessage(int code) {
        return MESSAGES.getOrDefault(code, "未知错误");
    }

    /**
     * 检查错误码是否存在
     */
    public static boolean exists(int code) {
        return MESSAGES.containsKey(code);
    }
}
