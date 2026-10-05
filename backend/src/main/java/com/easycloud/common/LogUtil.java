package com.easycloud.common;

import com.easycloud.entity.SysLog;
import com.easycloud.mapper.SysLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 操作日志 - 对应 PHP 记录 yixi_log
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogUtil {

    private final SysLogMapper sysLogMapper;

    public void log(String uid, String type, String data, String ip, String city) {
        try {
            SysLog entry = new SysLog();
            entry.setUid(uid);
            entry.setType(type);
            entry.setData(data);
            entry.setIp(ip);
            entry.setCity(city);
            entry.setDate(LocalDateTime.now());
            sysLogMapper.insert(entry);
        } catch (Exception e) {
            log.warn("写入操作日志失败: {}", e.getMessage());
        }
    }

    public void log(String uid, String type, String data, String ip) {
        log(uid, type, data, ip, null);
    }
}
