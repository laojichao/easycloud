package com.easycloud.controller.api;

import com.easycloud.common.LanzouResolver;
import com.easycloud.entity.App;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用初始化配置处理器
 * <p>
 * 处理客户端启动时的初始化请求，返回应用版本信息、更新配置等。
 * 属于白名单接口，不需要加密和签名验证。
 * <p>
 * 返回数据包括：版本号、更新说明、更新地址（支持蓝奏云）、强制更新开关、API调用量。
 * <p>
 * 对应原 PHP 文件: api/api/ini/index.php
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@Component
public class IniHandler {

    /**
     * 处理请求 - 返回格式兼容 PHP out(200, $ini_data, $app_res)
     */
    public Map<String, Object> handle(App app, Map<String, String> dataArr, String value) {
        Map<String, Object> iniData = new LinkedHashMap<>();
        iniData.put("version", app.getVersion());
        iniData.put("version_info", app.getVersionInfo());
        iniData.put("app_update_show", app.getAppUpdateShow());
        iniData.put("app_update_url", resolveUpdateUrl(app));
        iniData.put("app_update_must", app.getAppUpdateMust());
        iniData.put("api_total", app.getTotal());

        return ApiController.buildSuccessResponse(iniData, app, value);
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
