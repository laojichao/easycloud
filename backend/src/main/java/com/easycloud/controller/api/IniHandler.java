package com.easycloud.controller.api;

import com.easycloud.common.LanzouResolver;
import com.easycloud.entity.App;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获取应用配置 - 对应 PHP api/api/ini/index.php
 */
@Slf4j
@Component
public class IniHandler {

    /**
     * 处理请求 - 返回格式兼容 PHP out(200, $ini_data, $app_res)
     */
    public Map<String, Object> handle(App app, Map<String, String> dataArr) {
        Map<String, Object> iniData = new LinkedHashMap<>();
        iniData.put("version", app.getVersion());
        iniData.put("version_info", app.getVersionInfo());
        iniData.put("app_update_show", app.getAppUpdateShow());
        iniData.put("app_update_url", resolveUpdateUrl(app));
        iniData.put("app_update_must", app.getAppUpdateMust());
        iniData.put("api_total", app.getTotal());

        return ApiController.buildSuccessResponse(iniData, app, null);
    }

    /**
     * 解析更新 URL，支持蓝奏云链接
     */
    private String resolveUpdateUrl(App app) {
        String url = app.getAppUpdateUrl();
        if (url == null || url.isEmpty()) {
            return "";
        }

        if ("lanzou".equals(app.getAppUpdateUrlType())) {
            return LanzouResolver.resolve(url, app.getLanzouPass());
        }
        return url;
    }
}
