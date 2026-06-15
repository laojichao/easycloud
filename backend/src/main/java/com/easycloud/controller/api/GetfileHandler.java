package com.easycloud.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.LanzouResolver;
import com.easycloud.entity.App;
import com.easycloud.entity.AppFile;
import com.easycloud.mapper.AppFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 文件列表获取处理器
 * <p>
 * 处理客户端获取应用文件下载列表的请求，返回启用状态的文件列表。
 * 支持蓝奏云链接自动解析为直接下载地址。
 * 属于白名单接口，不需要加密和签名验证。
 * <p>
 * 对应原 PHP 文件: api/api/getfile/index.php
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetfileHandler {

    private final AppFileMapper appFileMapper;

    /**
     * 处理获取文件列表请求
     *
     * @param app     应用配置对象
     * @param dataArr 请求参数，可选 id 参数用于筛选特定文件
     * @param value   客户端传入的 value 参数，用于 check 字段计算
     * @return 包含文件列表的响应 Map
     */
    public Map<String, Object> handle(App app, Map<String, String> dataArr, String value) {
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
                fileUrl = LanzouResolver.resolve(fileUrl, file.getLanzouPass());
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("file_url", fileUrl);
            // PHP: date 字段为原始数据库 datetime 字符串格式 "yyyy-MM-dd HH:mm:ss"
            item.put("date", file.getAddtime() != null ?
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(file.getAddtime()) : "");
            item.put("note", file.getNote());
            fileList.add(item);
        }

        // PHP: out(200, $ret, $app_res) 或 out(201, '该应用下无外链', $app_res)
        if (fileList.isEmpty()) {
            return ApiController.buildErrorResponse(201, "该应用下无外链", app, value);
        }
        return ApiController.buildSuccessResponse(fileList, app, value);
    }
}
