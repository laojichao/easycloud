package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Md5Util;
import com.easycloud.common.Result;
import com.easycloud.entity.User;
import com.easycloud.mapper.UserMapper;
import com.easycloud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户管理控制器
 * <p>
 * 提供管理后台对注册用户的增删改查和余额调整功能。
 * 响应中自动隐藏密码字段（pwd=null）。
 * <p>
 * 对应原 PHP 项目中 yixi_user 表的管理操作
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final UserService userService;

    /**
     * 用户列表（分页）
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUser, keyword)
                    .or().like(User::getQq, keyword)
                    .or().like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getUid);

        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 隐藏密码字段
        result.getRecords().forEach(u -> u.setPwd(null));
        return Result.ok(result);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{uid}")
    public Result<?> getById(@PathVariable Long uid) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setPwd(null);
        return Result.ok(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<?> create(@RequestBody Map<String, String> body) {
        String username = body.get("user");
        String password = body.get("pwd");

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.fail("用户名和密码不能为空");
        }

        try {
            String qq = StringUtils.hasText(body.get("qq")) ? body.get("qq") : null;
            String email = StringUtils.hasText(body.get("email")) ? body.get("email") : null;
            User user = userService.register(username, password,
                    body.getOrDefault("ip", ""), qq, email);
            user.setPwd(null);
            return Result.ok("创建成功", user);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 编辑用户
     */
    @PutMapping("/{uid}")
    public Result<?> update(@PathVariable Long uid, @RequestBody Map<String, Object> body) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        if (body.containsKey("user")) {
            String newUser = (String) body.get("user");
            // 检查用户名是否已被其他人使用
            Long existing = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getUser, newUser)
                            .ne(User::getUid, uid));
            if (existing > 0) {
                return Result.fail("用户名已存在");
            }
            user.setUser(newUser);
        }
        if (body.containsKey("pwd") && StringUtils.hasText((String) body.get("pwd"))) {
            user.setPwd(Md5Util.encryptPassword((String) body.get("pwd")));
        }
        if (body.containsKey("qq")) {
            user.setQq((String) body.get("qq"));
        }
        if (body.containsKey("email")) {
            user.setEmail((String) body.get("email"));
        }
        if (body.containsKey("rmb")) {
            user.setRmb(new BigDecimal(body.get("rmb").toString()));
        }

        userService.updateUser(user);
        user.setPwd(null);
        return Result.ok("更新成功", user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{uid}")
    public Result<?> delete(@PathVariable Long uid) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        userService.deleteUser(uid);
        return Result.ok("删除成功");
    }

    /**
     * 调整余额
     * 正数增加，负数减少
     */
    @PostMapping("/{uid}/rmb")
    public Result<?> adjustRmb(@PathVariable Long uid, @RequestBody Map<String, Object> body) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        if (!body.containsKey("amount")) {
            return Result.fail("金额不能为空");
        }

        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        try {
            userService.updateRmb(uid, amount);
            User updated = userService.getByUid(uid);
            updated.setPwd(null);
            return Result.ok("余额调整成功", updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
