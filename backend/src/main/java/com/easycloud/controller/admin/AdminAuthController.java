package com.easycloud.controller.admin;

import com.easycloud.common.JwtUtil;
import com.easycloud.common.Md5Util;
import com.easycloud.common.Result;
import com.easycloud.entity.User;
import com.easycloud.service.ConfigService;
import com.easycloud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证控制器
 * <p>
 * 处理管理员登录、信息获取和 SSO 模拟登录功能。
 * 管理员账号密码存储在系统配置（yixi_config）中，支持明文和 MD5 两种密码存储方式。
 * 登录成功后返回 JWT Token，后续管理后台请求通过 Token 鉴权。
 * <p>
 * 对应原 PHP 文件: admin/login.php
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final JwtUtil jwtUtil;
    private final ConfigService configService;
    private final UserService userService;

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

        // 密码验证: 仅支持 MD5 哈希比对，禁止明文密码回退（安全要求）
        String encryptedPwd = Md5Util.encryptPassword(password);
        boolean passwordMatch = encryptedPwd.equals(configPwd);

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

    /**
     * SSO 模拟登录 - 管理员模拟登录为指定用户
     * <p>
     * 生成以用户 UID 为 subject 的 JWT Token，用于管理员以普通用户身份进行调试操作。
     *
     * @param uid 用户 UID
     * @return 包含 token 和用户信息的结果
     */
    @PostMapping("/sso/{uid}")
    public Result<Map<String, Object>> sso(@PathVariable Long uid) {
        if (uid == null || uid <= 0) {
            return Result.fail("无效的用户ID");
        }

        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 生成 JWT Token，subject = uid.toString()
        String token = jwtUtil.generateToken(uid.toString());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("uid", uid);
        data.put("username", user.getUser());

        return Result.ok("模拟登录成功", data);
    }
}
