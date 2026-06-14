package com.easycloud.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.entity.App;
import com.easycloud.entity.AppKm;
import com.easycloud.mapper.AppKmMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 卡密登录 - 对应 PHP api/api/kmlogon/index.php
 */
@Component
@RequiredArgsConstructor
public class KmlogonHandler {

    private final AppKmMapper appKmMapper;

    /** 时长类型对应的秒数 */
    private static final Map<String, Long> TIME_UNIT_SECONDS = Map.of(
            "hour", 3600L,
            "day", 86400L,
            "week", 604800L,
            "month", 2592000L,
            "season", 7776000L,
            "year", 31104000L
    );

    /** 永久卡哨兵值 */
    private static final String LONG_USE_SENTINEL = "4102243200";
    /** 次数卡无限次哨兵值 */
    private static final int SINGLE_UNLIMITED = 999999999;

    @Transactional
    public Map<String, Object> handle(App app, Map<String, String> dataArr, HttpServletRequest request) {
        // 参数校验（PHP: purge($data_arr['kami'])）
        String kami = dataArr.get("kami");
        if (kami == null || kami.trim().isEmpty()) {
            return ApiController.buildErrorResponse(148, "卡密不能为空", app, null);
        }
        kami = kami.trim();

        String markcode = dataArr.get("markcode");
        if (markcode == null || markcode.trim().isEmpty()) {
            return ApiController.buildErrorResponse(112, "机器码不能为空", app, null);
        }
        markcode = markcode.trim();

        String clientIp = getClientIp(request);

        // 免费模式 - 直接登录
        if ("n".equals(app.getSwitch_())) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kami", kami);
            data.put("vip", LONG_USE_SENTINEL);
            return ApiController.buildSuccessResponse(data, app, null);
        }

        // 查询卡密 - PHP 使用 binary kami 做大小写敏感匹配
        AppKm km = appKmMapper.selectOne(
                new LambdaQueryWrapper<AppKm>()
                        .apply("BINARY kami = {0}", kami)
                        .eq(AppKm::getAppid, app.getId())
                        .last("LIMIT 1")
        );

        if (km == null) {
            return ApiController.buildErrorResponse(149, "卡密不存在", app, null);
        }

        // 检查机器码是否匹配
        if (!markcode.equals(km.getUser())) {
            if (km.getUser() != null && !km.getUser().isEmpty() && "y".equals(app.getLogonCheckIn())) {
                return ApiController.buildErrorResponse(150, "卡密已被使用", app, null);
            }
        }

        // 检查卡密状态
        if ("n".equals(km.getState())) {
            return ApiController.buildErrorResponse(151, "卡密已禁用", app, null);
        }

        // IP 验证
        if ("y".equals(app.getIpauth())) {
            if (km.getUserIp() != null && !km.getUserIp().isEmpty() && !km.getUserIp().equals(clientIp)) {
                return ApiController.buildErrorResponse(169, "IP地址不一致", app, null);
            }
        }

        long now = Instant.now().getEpochSecond();

        // 时长卡处理
        if ("code".equals(km.getType())) {
            return handleCodeType(app, km, kami, markcode, clientIp, now);
        }

        // 次数卡处理
        if ("single".equals(km.getType())) {
            return handleSingleType(app, km, kami, markcode, clientIp, now);
        }

        return ApiController.buildErrorResponse(201, "未知卡密类型", app, null);
    }

    /**
     * 时长卡处理
     */
    private Map<String, Object> handleCodeType(App app, AppKm km, String kami, String markcode, String clientIp, long now) {
        String kmTime = km.getKmTime();
        Long secondsPerUnit = TIME_UNIT_SECONDS.get(kmTime);

        // 全新卡密 - 首次使用
        if (km.getUseTime() == null || km.getUseTime() == 0) {
            String endTime;
            if ("longuse".equals(kmTime)) {
                endTime = LONG_USE_SENTINEL;
            } else if (secondsPerUnit != null) {
                endTime = String.valueOf(now + secondsPerUnit * (km.getAmount() != null ? km.getAmount() : 1));
            } else {
                endTime = String.valueOf(now + 86400L);
            }

            boolean success = appKmMapper.updateKmLogin(km.getId(), now, markcode, endTime, clientIp, "y") > 0;
            if (!success) {
                return ApiController.buildErrorResponse(201, "登录失败，请重试", app, null);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kami", kami);
            data.put("vip", endTime);
            return ApiController.buildSuccessResponse(data, app, null);
        }

        // 已使用过的卡密 - 检查是否到期
        String endTimeStr = km.getEndTime();
        if (LONG_USE_SENTINEL.equals(endTimeStr)) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kami", kami);
            data.put("vip", endTimeStr);
            return ApiController.buildSuccessResponse(data, app, null);
        }

        try {
            long endTime = Long.parseLong(endTimeStr);
            if (endTime > now) {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("kami", kami);
                data.put("vip", endTimeStr);
                return ApiController.buildSuccessResponse(data, app, null);
            } else {
                return ApiController.buildErrorResponse(201, "卡密已到期", app, null);
            }
        } catch (NumberFormatException e) {
            return ApiController.buildErrorResponse(201, "卡密状态异常", app, null);
        }
    }

    /**
     * 次数卡处理
     */
    private Map<String, Object> handleSingleType(App app, AppKm km, String kami, String markcode, String clientIp, long now) {
        int amount = km.getAmount() != null ? km.getAmount() : 0;

        if (amount <= 0) {
            return ApiController.buildErrorResponse(201, "卡密已到期", app, null);
        }

        // 全新卡密
        if (km.getUseTime() == null || km.getUseTime() == 0) {
            int newAmount = (amount == SINGLE_UNLIMITED) ? amount : amount - 1;

            boolean success = appKmMapper.updateSingleLogin(km.getId(), now, newAmount, markcode, clientIp, "y") > 0;
            if (!success) {
                return ApiController.buildErrorResponse(201, "登录失败，请重试", app, null);
            }

            String vip = String.valueOf(now + 3600);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kami", kami);
            data.put("vip", vip);
            return ApiController.buildSuccessResponse(data, app, null);
        }

        // 已使用过 - 扣减次数
        if (appKmMapper.decreaseAmount(km.getId()) <= 0) {
            return ApiController.buildErrorResponse(201, "登录失败，请重试", app, null);
        }
        String vip = String.valueOf(now + 3600);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("kami", kami);
        data.put("vip", vip);
        return ApiController.buildSuccessResponse(data, app, null);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            for (String part : ip.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.startsWith("10.") && !trimmed.startsWith("172.16.") && !trimmed.startsWith("192.168.")) {
                    return trimmed;
                }
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("CF-Connecting-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
