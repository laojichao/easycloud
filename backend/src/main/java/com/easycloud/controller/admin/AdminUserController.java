package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.LogUtil;
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
 * 用户管理 - 对应 PHP admin 用户列表/编辑/删户/调余额
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final LogUtil logUtil;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(required = false) String search) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(search)) {
            wrapper.and(w -> w.like(User::getUser, search)
                    .or().like(User::getQq, search)
                    .or().like(User::getEmail, search));
        }
        wrapper.orderByDesc(User::getUid);
        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 不回传密码
        result.getRecords().forEach(u -> u.setPwd(null));
        return Result.ok(result);
    }

    @GetMapping("/{uid}")
    public Result<?> getById(@PathVariable Long uid) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        user.setPwd(null);
        return Result.ok(user);
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(body.get("username"), body.get("password"),
                    body.get("qq"), body.get("email"), null);
            user.setPwd(null);
            return Result.ok("创建成功", user);
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @PutMapping("/{uid}")
    public Result<?> update(@PathVariable Long uid, @RequestBody Map<String, Object> body) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        User update = new User();
        update.setUid(uid);
        if (body.containsKey("qq")) {
            update.setQq(String.valueOf(body.get("qq")));
        }
        if (body.containsKey("email")) {
            update.setEmail(String.valueOf(body.get("email")));
        }
        String password = body.get("password") == null ? null : String.valueOf(body.get("password"));
        if (StringUtils.hasText(password)) {
            update.setPwd(userService.encodePassword(password));
        }
        userService.updateUser(update);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{uid}")
    public Result<?> delete(@PathVariable Long uid) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        userService.deleteUser(uid);
        return Result.ok("删除成功");
    }

    @PostMapping("/{uid}/rmb")
    public Result<?> adjustRmb(@PathVariable Long uid, @RequestBody Map<String, Object> body) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户信息异常");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(String.valueOf(body.get("amount")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "请输入有效金额");
        }
        try {
            userService.updateRmb(uid, amount);
        } catch (RuntimeException e) {
            return Result.fail(400, "调整失败");
        }
        return Result.ok("余额调整成功");
    }
}
