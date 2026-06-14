package com.easycloud.controller.admin;

import com.easycloud.common.Result;
import com.easycloud.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 系统设置 - 对应 PHP admin/set.php
 */
@RestController
@RequestMapping("/api/admin/setting")
@RequiredArgsConstructor
public class AdminSettingController {

    private final ConfigService configService;

    /** 敏感配置项 - 不允许通过接口读取或修改 */
    private static final Set<String> SENSITIVE_KEYS = Set.of("admin_pwd", "admin_user", "db_pwd", "mail_pwd");

    /**
     * 获取所有设置
     */
    @GetMapping
    public Result<?> getSettings() {
        Map<String, String> settings = configService.getAllSettings();
        // 移除所有敏感信息
        for (String key : SENSITIVE_KEYS) {
            settings.remove(key);
        }
        return Result.ok(settings);
    }

    /**
     * 获取单个设置
     */
    @GetMapping("/{key}")
    public Result<?> getSetting(@PathVariable String key) {
        if (SENSITIVE_KEYS.contains(key)) {
            return Result.fail("不允许读取敏感配置");
        }
        String value = configService.getSetting(key);
        return Result.ok(value);
    }

    /**
     * 保存设置
     */
    @PostMapping
    public Result<?> saveSettings(@RequestBody Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            // 不允许通过此接口修改敏感配置
            if (SENSITIVE_KEYS.contains(entry.getKey())) {
                continue;
            }
            configService.saveSetting(entry.getKey(), entry.getValue());
        }
        return Result.ok("保存成功");
    }

    /**
     * 刷新缓存
     */
    @PostMapping("/refresh-cache")
    public Result<?> refreshCache() {
        configService.refreshCache();
        return Result.ok("缓存刷新成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        String oldPwd = body.get("old_password");
        String newPwd = body.get("new_password");

        if (oldPwd == null || oldPwd.isEmpty()) {
            return Result.fail("旧密码不能为空");
        }
        if (newPwd == null || newPwd.length() < 6) {
            return Result.fail("新密码长度不能小于6位");
        }

        String currentPwd = configService.getSetting("admin_pwd");
        String encryptedOldPwd = md5(oldPwd + "!@#%!s!0");

        if (!encryptedOldPwd.equals(currentPwd)) {
            return Result.fail("旧密码错误");
        }

        String encryptedNewPwd = md5(newPwd + "!@#%!s!0");
        configService.saveSetting("admin_pwd", encryptedNewPwd);

        return Result.ok("密码修改成功");
    }

    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xFF));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
