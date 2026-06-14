package com.easycloud.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.entity.App;
import com.easycloud.entity.AppFile;
import com.easycloud.mapper.AppFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 获取文件列表 - 对应 PHP api/api/getfile/index.php
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetfileHandler {

    private final AppFileMapper appFileMapper;

    public Map<String, Object> handle(App app, Map<String, String> dataArr) {
        Long appId = app.getId();

        LambdaQueryWrapper<AppFile> wrapper = new LambdaQueryWrapper<AppFile>()
                .eq(AppFile::getAppid, appId)
                .eq(AppFile::getState, "y")
                .orderByDesc(AppFile::getId);

        // 如果指定了文件ID
        if (dataArr.containsKey("id") && !dataArr.get("id").isEmpty()) {
            try {
                Long fileId = Long.parseLong(dataArr.get("id"));
                wrapper.eq(AppFile::getId, fileId);
            } catch (NumberFormatException ignored) {
            }
        }

        List<AppFile> files = appFileMapper.selectList(wrapper);

        List<Map<String, Object>> fileList = new ArrayList<>();
        for (AppFile file : files) {
            String fileUrl = file.getFileUrl();

            // 处理蓝奏云链接
            if ("lanzou".equals(file.getType())) {
                fileUrl = resolveLanzouUrl(fileUrl, file.getLanzouPass());
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("file_url", fileUrl);
            item.put("date", file.getAddtime() != null ? file.getAddtime().toString() : "");
            item.put("note", file.getNote());
            fileList.add(item);
        }

        // PHP: out(200, $ret, $app_res) 或 out(201, '该应用下无外链', $app_res)
        if (fileList.isEmpty()) {
            return ApiController.buildErrorResponse(201, "该应用下无外链", app, null);
        }
        return ApiController.buildSuccessResponse(fileList, app, null);
    }

    private String resolveLanzouUrl(String url, String pwd) {
        try {
            // 蓝奏云链接解析 - 简化版，返回原始URL
            // 实际需要复杂的爬虫逻辑
            return url;
        } catch (Exception e) {
            log.warn("解析蓝奏云链接失败: {}", url, e);
            return url;
        }
    }
}
