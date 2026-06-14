package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.App;
import com.easycloud.mapper.AppMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 应用管理 - 对应 PHP admin/applist.php, appedit.php
 */
@RestController
@RequestMapping("/api/admin/app")
@RequiredArgsConstructor
public class AdminAppController {

    private final AppMapper appMapper;

    /**
     * 应用列表
     */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(App::getName, keyword)
                    .or().like(App::getNote, keyword);
        }
        wrapper.orderByDesc(App::getId);

        Page<App> result = appMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    /**
     * 获取单个应用
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        App app = appMapper.selectById(id);
        if (app == null) {
            return Result.fail("应用不存在");
        }
        return Result.ok(app);
    }

    /**
     * 创建应用
     */
    @PostMapping
    public Result<?> create(@RequestBody App app) {
        app.setDate(LocalDateTime.now());
        app.setTotal("0");
        if (app.getAppkey() == null || app.getAppkey().isEmpty()) {
            app.setAppkey(generateAppKey());
        }
        appMapper.insert(app);
        return Result.ok("创建成功", app);
    }

    /**
     * 更新应用
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody App app) {
        app.setId(id);
        appMapper.updateById(app);
        return Result.ok("更新成功");
    }

    /**
     * 删除应用
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        appMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    /**
     * 切换应用状态（active/switch/ipauth/login_check）
     */
    @PostMapping("/{id}/toggle")
    public Result<?> toggle(@PathVariable Long id, @RequestBody Map<String, String> body) {
        App app = appMapper.selectById(id);
        if (app == null) {
            return Result.fail("应用不存在");
        }

        String field = body.get("field");
        String value = body.get("value");

        if (field == null || value == null) {
            return Result.fail("参数不完整");
        }

        switch (field) {
            case "active":
                app.setActive(value);
                break;
            case "switch":
                app.setSwitch_(value);
                break;
            case "ipauth":
                app.setIpauth(value);
                break;
            case "logon_check_in":
                app.setLogonCheckIn(value);
                break;
            case "mi_state":
                app.setMiState(value);
                break;
            case "mi_sign":
                app.setMiSign(value);
                break;
            case "mi_sign_in":
                app.setMiSignIn(value);
                break;
            default:
                return Result.fail("不支持的字段: " + field);
        }

        appMapper.updateById(app);
        return Result.ok("切换成功");
    }

    /**
     * 重新生成 appkey
     */
    @PostMapping("/{id}/regenkey")
    public Result<?> regenerateKey(@PathVariable Long id) {
        App app = appMapper.selectById(id);
        if (app == null) {
            return Result.fail("应用不存在");
        }

        String newKey = generateAppKey();
        app.setAppkey(newKey);
        appMapper.updateById(app);

        Map<String, String> data = new HashMap<>();
        data.put("appkey", newKey);
        return Result.ok("重新生成成功", data);
    }

    /**
     * 批量操作
     */
    @PostMapping("/batch")
    public Result<?> batch(@RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        @SuppressWarnings("unchecked")
        List<Number> ids = (List<Number>) body.get("ids");

        if (action == null || ids == null || ids.isEmpty()) {
            return Result.fail("参数不完整");
        }

        for (Number idNum : ids) {
            Long id = idNum.longValue();
            App app = appMapper.selectById(id);
            if (app == null) continue;

            switch (action) {
                case "enable":
                    app.setActive("y");
                    appMapper.updateById(app);
                    break;
                case "disable":
                    app.setActive("n");
                    appMapper.updateById(app);
                    break;
                case "delete":
                    appMapper.deleteById(id);
                    break;
            }
        }

        return Result.ok("批量操作成功");
    }

    private String generateAppKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
}
