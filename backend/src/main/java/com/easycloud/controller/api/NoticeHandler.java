package com.easycloud.controller.api;

import com.easycloud.entity.App;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获取应用公告 - 对应 PHP api/api/notice/index.php
 */
@Component
public class NoticeHandler {

    public Map<String, Object> handle(App app, Map<String, String> dataArr) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("app_gg", app.getAppGg());

        // PHP: out(200, $ini_data, $app_res) -> msg 字段放数据
        return ApiController.buildSuccessResponse(data, app, null);
    }
}
