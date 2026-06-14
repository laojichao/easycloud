package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.AppFile;
import com.easycloud.mapper.AppFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理 - 对应 PHP admin/appfilelist.php, addappfile.php, appfileedit.php
 */
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
        file.setAddtime(LocalDateTime.now());
        if (file.getState() == null) {
            file.setState("y");
        }
        appFileMapper.insert(file);
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
