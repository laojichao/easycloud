package com.easycloud.common;

import com.easycloud.entity.SysLog;
import com.easycloud.mapper.SysLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志记录工具类
 * <p>
 * 对应原 PHP includes/core.func.php 中的 log_result() 函数。
 * 提供统一的操作日志记录功能，用于审计用户行为和系统事件。
 * <p>
 * 使用方式：在需要记录日志的 Service 或 Controller 中注入 LogUtil，
 * 调用 {@link #log(String, String, String, String, String)} 方法记录日志。
 * <p>
 * 典型使用场景：
 * <ul>
 *   <li>用户登录/注册时记录认证日志</li>
 *   <li>卡密使用时记录授权日志</li>
 *   <li>支付行为记录交易日志</li>
 *   <li>管理员操作记录管理日志</li>
 * </ul>
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class LogUtil {

    private final SysLogMapper sysLogMapper;

    /**
     * 记录操作日志
     * <p>
     * 将操作日志写入 yixi_log 表，包含操作用户、日志类型、详细数据、IP 和归属地信息。
     *
     * @param uid  操作用户标识（字符串类型，兼容匿名用户如 "0"）
     * @param type 日志类型/操作分类（如 "login"、"reg"、"usekm"、"pay"）
     * @param data 日志详细数据/操作描述
     * @param ip   客户端 IP 地址
     * @param city IP 归属地城市（可为 null）
     */
    public void log(String uid, String type, String data, String ip, String city) {
        SysLog log = new SysLog();
        log.setUid(uid);
        log.setType(type);
        log.setData(data);
        log.setIp(ip);
        log.setCity(city);
        log.setDate(LocalDateTime.now());
        sysLogMapper.insert(log);
    }

    /**
     * 记录操作日志（简化版，不含城市信息）
     *
     * @param uid  操作用户标识
     * @param type 日志类型
     * @param data 日志详细数据
     * @param ip   客户端 IP 地址
     */
    public void log(String uid, String type, String data, String ip) {
        log(uid, type, data, ip, null);
    }
}
