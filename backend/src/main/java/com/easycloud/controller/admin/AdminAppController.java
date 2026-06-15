package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.App;
import com.easycloud.entity.AppFile;
import com.easycloud.entity.AppKm;
import com.easycloud.entity.AppUser;
import com.easycloud.entity.UserJk;
import com.easycloud.mapper.AppFileMapper;
import com.easycloud.mapper.AppKmMapper;
import com.easycloud.mapper.AppMapper;
import com.easycloud.mapper.AppUserMapper;
import com.easycloud.mapper.UserJkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 应用管理控制器
 * <p>
 * 提供应用的增删改查、配置更新、图标上传、状态切换等管理功能。
 * 对应原 PHP 项目中多个管理页面的功能：
 * <ul>
 *   <li>applist.php - 应用列表</li>
 *   <li>appedit.php - 应用编辑</li>
 *   <li>addapp.php - 应用添加</li>
 *   <li>appkmlist.php - 卡密管理（部分）</li>
 * </ul>
 * 删除应用时会级联删除关联的卡密、文件、用户绑定等数据。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/app")
@RequiredArgsConstructor
public class AdminAppController {

    @Value("${app.upload.dir:./uploads/images}")
    private String uploadDir;

    private final AppMapper appMapper;
    private final AppKmMapper appKmMapper;
    private final AppFileMapper appFileMapper;
    private final AppUserMapper appUserMapper;
    private final UserJkMapper userJkMapper;

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
        // PHP addapp: 检查应用名是否重复
        if (app.getName() != null && !app.getName().isEmpty()) {
            Long existing = appMapper.selectCount(
                    new LambdaQueryWrapper<App>().eq(App::getName, app.getName()));
            if (existing > 0) {
                return Result.fail("应用名称已存在");
            }
        }
        app.setDate(LocalDateTime.now());
        app.setTotal("0");
        if (app.getActive() == null) app.setActive("y");
        if (app.getAppkey() == null || app.getAppkey().isEmpty()) {
            app.setAppkey(generateAppKey());
        }
        appMapper.insert(app);
        log.info("创建应用: id={}, name={}", app.getId(), app.getName());
        return Result.ok("创建成功", app);
    }

    /**
     * 更新应用
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody App app) {
        // PHP appedit: 检查应用名是否重复（排除自身）
        if (app.getName() != null && !app.getName().isEmpty()) {
            Long existing = appMapper.selectCount(
                    new LambdaQueryWrapper<App>()
                            .eq(App::getName, app.getName())
                            .ne(App::getId, id));
            if (existing > 0) {
                return Result.fail("应用名称已存在");
            }
        }
        app.setId(id);
        appMapper.updateById(app);
        log.info("更新应用: id={}", id);
        return Result.ok("更新成功");
    }

    /**
     * 更新安全配置（对应 PHP editApp_safe）
     */
    @PutMapping("/{id}/security")
    public Result<?> updateSecurity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        App app = appMapper.selectById(id);
        if (app == null) return Result.fail("应用不存在");

        if (body.containsKey("miState")) app.setMiState((String) body.get("miState"));
        if (body.containsKey("miType")) app.setMiType(((Number) body.get("miType")).intValue());
        if (body.containsKey("miSign")) app.setMiSign((String) body.get("miSign"));
        if (body.containsKey("miSignIn")) app.setMiSignIn((String) body.get("miSignIn"));
        if (body.containsKey("printSign")) app.setPrintSign((String) body.get("printSign"));
        if (body.containsKey("rc4Key")) app.setRc4Key((String) body.get("rc4Key"));
        if (body.containsKey("miTime")) app.setMiTime(((Number) body.get("miTime")).intValue());

        appMapper.updateById(app);
        return Result.ok("安全配置更新成功");
    }

    /**
     * 更新卡密/认证配置（对应 PHP kmedit）
     */
    @PutMapping("/{id}/auth")
    public Result<?> updateAuth(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        App app = appMapper.selectById(id);
        if (app == null) return Result.fail("应用不存在");

        if (body.containsKey("switch")) app.setSwitch_((String) body.get("switch"));
        if (body.containsKey("ipauth")) app.setIpauth((String) body.get("ipauth"));
        if (body.containsKey("logonCheckIn")) app.setLogonCheckIn((String) body.get("logonCheckIn"));
        if (body.containsKey("kmUnmachine")) app.setKmUnmachine((String) body.get("kmUnmachine"));
        if (body.containsKey("kmChange")) app.setKmChange(((Number) body.get("kmChange")).intValue());
        if (body.containsKey("kmChangeNum")) app.setKmChangeNum(((Number) body.get("kmChangeNum")).intValue());
        if (body.containsKey("kmChangeTime")) app.setKmChangeTime(((Number) body.get("kmChangeTime")).intValue());
        if (body.containsKey("longuseKmChange")) app.setLonguseKmChange(((Number) body.get("longuseKmChange")).intValue());
        if (body.containsKey("singleKmChangeNum")) app.setSingleKmChangeNum(((Number) body.get("singleKmChangeNum")).intValue());

        appMapper.updateById(app);
        return Result.ok("认证配置更新成功");
    }

    /**
     * 更新应用信息（对应 PHP appedit / editApp_update）
     */
    @PutMapping("/{id}/info")
    public Result<?> updateInfo(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        App app = appMapper.selectById(id);
        if (app == null) return Result.fail("应用不存在");

        if (body.containsKey("name")) app.setName((String) body.get("name"));
        if (body.containsKey("img")) app.setImg((String) body.get("img"));
        if (body.containsKey("note")) app.setNote((String) body.get("note"));
        if (body.containsKey("appGg")) app.setAppGg((String) body.get("appGg"));
        if (body.containsKey("version")) app.setVersion((String) body.get("version"));
        if (body.containsKey("versionInfo")) app.setVersionInfo((String) body.get("versionInfo"));
        if (body.containsKey("active")) app.setActive((String) body.get("active"));
        if (body.containsKey("appUpdateUrl")) app.setAppUpdateUrl((String) body.get("appUpdateUrl"));
        if (body.containsKey("appUpdateShow")) app.setAppUpdateShow((String) body.get("appUpdateShow"));
        if (body.containsKey("appUpdateMust")) app.setAppUpdateMust((String) body.get("appUpdateMust"));
        if (body.containsKey("appUpdateUrlType")) app.setAppUpdateUrlType((String) body.get("appUpdateUrlType"));
        if (body.containsKey("lanzouPass")) app.setLanzouPass((String) body.get("lanzouPass"));

        appMapper.updateById(app);
        return Result.ok("应用信息更新成功");
    }

    /**
     * 删除应用（级联删除关联的卡密和文件）
     */
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id) {
        // PHP appdel: 级联删除 yixi_appkm, yixi_appfile, yixi_appuser, yixi_userjk
        appKmMapper.delete(new LambdaQueryWrapper<AppKm>().eq(AppKm::getAppid, id));
        appFileMapper.delete(new LambdaQueryWrapper<AppFile>().eq(AppFile::getAppid, id));
        appUserMapper.delete(new LambdaQueryWrapper<AppUser>().eq(AppUser::getAppid, id));
        userJkMapper.delete(new LambdaQueryWrapper<UserJk>().eq(UserJk::getAppid, id));
        appMapper.deleteById(id);
        log.info("删除应用(级联): id={}", id);
        return Result.ok("删除成功");
    }

    /**
     * 上传应用图标 - 对应 PHP uploadappimg
     */
    @PostMapping("/upload-image")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("请选择文件");
        }

        // 验证文件类型（PHP: png/jpg/gif/jpeg/webp/bmp）
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            return Result.fail("文件名无效");
        }
        String ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
        Set<String> allowedExts = Set.of("png", "jpg", "jpeg", "gif", "webp", "bmp");
        if (!allowedExts.contains(ext)) {
            return Result.fail("不支持的图片格式，仅支持: png/jpg/gif/jpeg/webp/bmp");
        }

        // 验证文件大小（最大 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail("文件大小不能超过5MB");
        }

        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 返回相对路径
            String relativePath = "/uploads/images/" + fileName;
            Map<String, String> data = new HashMap<>();
            data.put("url", relativePath);
            return Result.ok("上传成功", data);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败，请稍后重试");
        }
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
    @Transactional
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
                    // 级联删除关联数据（与单个删除逻辑一致）
                    appKmMapper.delete(new LambdaQueryWrapper<AppKm>().eq(AppKm::getAppid, id));
                    appFileMapper.delete(new LambdaQueryWrapper<AppFile>().eq(AppFile::getAppid, id));
                    appUserMapper.delete(new LambdaQueryWrapper<AppUser>().eq(AppUser::getAppid, id));
                    userJkMapper.delete(new LambdaQueryWrapper<UserJk>().eq(UserJk::getAppid, id));
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
