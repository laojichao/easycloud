package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.AppFile;
import com.easycloud.mapper.AppFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 * <p>
 * 提供应用文件链接的增删改查和批量操作功能。
 * 文件链接支持直链和蓝奏云网盘，客户端通过 getfile API 获取文件列表。
 * <p>
 * 对应原 PHP 项目:
 * <ul>
 *   <li>appfilelist.php - 文件列表</li>
 *   <li>addappfile.php - 添加文件</li>
 *   <li>appfileedit.php - 编辑文件</li>
 * </ul>
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
public class AdminFileController {

    private final AppFileMapper appFileMapper;

    /**
     * 文件列表
     */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long appid,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<AppFile> wrapper = new LambdaQueryWrapper<>();
        if (appid != null) {
            wrapper.eq(AppFile::getAppid, appid);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AppFile::getNote, keyword)
                    .or().like(AppFile::getFileUrl, keyword);
        }
        wrapper.orderByDesc(AppFile::getId);

        Page<AppFile> result = appFileMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    /**
     * 创建文件
     */
    @PostMapping
    public Result<?> create(@RequestBody AppFile file) {
        // PHP addappfile: URL 格式验证
        if (file.getFileUrl() == null || file.getFileUrl().isEmpty()) {
            return Result.fail("文件链接不能为空");
        }
        if (!file.getFileUrl().matches("^https?://.+")) {
            return Result.fail("文件链接格式不正确，需以 http:// 或 https:// 开头");
        }

        // PHP addappfile: 检查链接是否重复
        Long existing = appFileMapper.selectCount(
                new LambdaQueryWrapper<AppFile>()
                        .eq(AppFile::getFileUrl, file.getFileUrl()));
        if (existing > 0) {
            return Result.fail("该文件链接已存在");
        }

        file.setAddtime(LocalDateTime.now());
        if (file.getState() == null) {
            file.setState("y");
        }
        appFileMapper.insert(file);
        log.info("创建文件记录: id={}, appid={}, url={}", file.getId(), file.getAppid(), file.getFileUrl());
        return Result.ok("创建成功", file);
    }

    /**
     * 更新文件
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody AppFile file) {
        file.setId(id);
        appFileMapper.updateById(file);
        return Result.ok("更新成功");
    }

    /**
     * 切换单个文件状态（对应 PHP app_fileactive）
     */
    @PostMapping("/{id}/toggle")
    public Result<?> toggle(@PathVariable Long id) {
        AppFile file = appFileMapper.selectById(id);
        if (file == null) {
            return Result.fail("文件不存在");
        }
        file.setState("y".equals(file.getState()) ? "n" : "y");
        appFileMapper.updateById(file);
        return Result.ok("操作成功");
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        appFileMapper.deleteById(id);
        log.info("删除文件记录: id={}", id);
        return Result.ok("删除成功");
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
            AppFile file = appFileMapper.selectById(id);
            if (file == null) continue;

            switch (action) {
                case "enable":
                    file.setState("y");
                    appFileMapper.updateById(file);
                    break;
                case "disable":
                    file.setState("n");
                    appFileMapper.updateById(file);
                    break;
                case "delete":
                    appFileMapper.deleteById(id);
                    break;
            }
        }

        return Result.ok("批量操作成功");
    }
}
