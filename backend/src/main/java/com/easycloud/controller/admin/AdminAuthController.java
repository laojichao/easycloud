package com.easycloud.controller.admin;

import com.easycloud.common.JwtUtil;
import com.easycloud.common.Result;
import com.easycloud.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证 - 对应 PHP admin/login.php
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final JwtUtil jwtUtil;
    private final ConfigService configService;

    /** 密码盐值 - 对应 PHP $password_hash = '!@#%!s!0' */
    private static final String PASSWORD_SALT = "!@#%!s!0";

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return Result.fail("密码不能为空");
        }

        // 获取配置中的管理员账号
        String configUser = configService.getSetting("admin_user");
        String configPwd = configService.getSetting("admin_pwd");

        if (configUser == null || configPwd == null) {
            return Result.fail("系统未配置管理员账号");
        }

        // 密码验证: 兼容 PHP 明文存储 和 Java md5 存储
        String encryptedPwd = md5(password + PASSWORD_SALT);
        boolean passwordMatch = password.equals(configPwd) || encryptedPwd.equals(configPwd);

        if (!username.equals(configUser) || !passwordMatch) {
            return Result.fail("用户名或密码错误");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(username);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", username);

        return Result.ok("登录成功", data);
    }

    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/info")
    public Result<?> info(@RequestAttribute(value = "adminUser", required = false) String adminUser) {
        if (adminUser == null) {
            return Result.fail(401, "未登录");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", adminUser);
        data.put("sitename", configService.getSetting("sitename"));
        return Result.ok(data);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
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
