package com.easycloud.controller.api;

import com.easycloud.entity.App;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用公告处理器
 * <p>
 * 处理客户端获取应用公告的请求，返回应用的 appGg 字段内容。
 * 属于白名单接口，不需要加密和签名验证。
 * <p>
 * 对应原 PHP 文件: api/api/notice/index.php
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Component
public class NoticeHandler {

    /**
     * 处理获取公告请求
     *
     * @param app     应用配置对象
     * @param dataArr 请求参数（此接口不使用额外参数）
     * @return 包含公告内容的响应 Map
     */
    public Map<String, Object> handle(App app, Map<String, String> dataArr) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("app_gg", app.getAppGg());

        // PHP: out(200, $ini_data, $app_res) -> msg 字段放数据
        return ApiController.buildSuccessResponse(data, app, null);
    }
}
