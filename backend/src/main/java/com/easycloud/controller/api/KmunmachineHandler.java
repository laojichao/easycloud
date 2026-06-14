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
 * 卡密解绑 - 对应 PHP api/api/kmunmachine/index.php
 */
@Component
@RequiredArgsConstructor
public class KmunmachineHandler {

    private final AppKmMapper appKmMapper;

    private static final String LONG_USE_SENTINEL = "4102243200";
    private static final int SINGLE_UNLIMITED = 999999999;

    @Transactional
    public Map<String, Object> handle(App app, Map<String, String> dataArr, HttpServletRequest request) {
        // 参数校验
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

        // 免费模式禁止解绑
        if ("n".equals(app.getSwitch_())) {
            return ApiController.buildErrorResponse(201, "解绑失败,无权限", app, null);
        }

        // 查询卡密（PHP kmunmachine 不使用 binary）
        AppKm km = appKmMapper.selectOne(
                new LambdaQueryWrapper<AppKm>()
                        .eq(AppKm::getKami, kami)
                        .eq(AppKm::getAppid, app.getId())
                        .last("LIMIT 1")
        );

        if (km == null) {
            return ApiController.buildErrorResponse(149, "卡密不存在", app, null);
        }

        // 卡密状态检查
        if ("n".equals(km.getState())) {
            return ApiController.buildErrorResponse(151, "卡密已禁用", app, null);
        }

        // 机器码匹配检查
        if ("y".equals(app.getKmUnmachine())) {
            if (!markcode.equals(km.getUser())) {
                return ApiController.buildErrorResponse(201, "解绑失败,无权限", app, null);
            }
        }

        // 未使用卡密无需解绑
        if (km.getUseTime() == null || km.getUseTime() == 0) {
            return ApiController.buildErrorResponse(202, "全新卡密,无需解绑", app, null);
        }

        long now = Instant.now().getEpochSecond();

        // 时长卡解绑
        if ("code".equals(km.getType())) {
            return handleCodeUnbind(app, km, now);
        }

        // 次数卡解绑
        if ("single".equals(km.getType())) {
            return handleSingleUnbind(app, km, now);
        }

        return ApiController.buildErrorResponse(201, "未知卡密类型", app, null);
    }

    /**
     * 时长卡解绑
     */
    private Map<String, Object> handleCodeUnbind(App app, AppKm km, long now) {
        String kmTime = km.getKmTime();

        // 永久卡
        if ("longuse".equals(kmTime)) {
            int kmChange = km.getKmChange() != null ? km.getKmChange() : 0;
            int longuseKmChange = app.getLonguseKmChange() != null ? app.getLonguseKmChange() : 0;
            if (kmChange >= longuseKmChange) {
                return ApiController.buildErrorResponse(201, "解绑失败,可解绑次数不足", app, null);
            }

            if (km.getKmChangeTime() != null && km.getKmChangeTime() > 0) {
                int kmChangeTime = app.getKmChangeTime() != null ? app.getKmChangeTime() : 0;
                if ((now - km.getKmChangeTime()) < (3600L * kmChangeTime)) {
                    return ApiController.buildErrorResponse(201, "解绑失败,换绑时间不足", app, null);
                }
            }

            appKmMapper.unbindKm(km.getId(), now);
            return ApiController.buildSuccessResponse("卡密解绑成功", app, null);
        }

        // 贵宾卡
        if ("vipcard".equals(kmTime)) {
            return ApiController.buildErrorResponse(202, "贵宾卡无需解绑", app, null);
        }

        // 普通时长卡
        String endTimeStr = km.getEndTime();
        try {
            long endTime = Long.parseLong(endTimeStr);
            if (endTime < now) {
                return ApiController.buildErrorResponse(201, "解绑失败,卡密时长不足", app, null);
            }
        } catch (NumberFormatException e) {
            return ApiController.buildErrorResponse(201, "卡密状态异常", app, null);
        }

        if (km.getKmChangeTime() != null && km.getKmChangeTime() > 0) {
            int kmChangeTime = app.getKmChangeTime() != null ? app.getKmChangeTime() : 0;
            if ((now - km.getKmChangeTime()) < (3600L * kmChangeTime)) {
                return ApiController.buildErrorResponse(201, "解绑失败,换绑时间不足", app, null);
            }
        }

        int kmChange = km.getKmChange() != null ? km.getKmChange() : 0;
        int maxKmChange = app.getKmChange() != null ? app.getKmChange() : 0;
        if (kmChange >= maxKmChange) {
            return ApiController.buildErrorResponse(201, "解绑失败,可解绑次数不足", app, null);
        }

        // 计算扣除时长后的结束时间
        int kmChangeNum = app.getKmChangeNum() != null ? app.getKmChangeNum() : 0;
        long newEndTime;
        try {
            newEndTime = Long.parseLong(km.getEndTime()) - (3600L * kmChangeNum);
        } catch (NumberFormatException e) {
            newEndTime = now;
        }

        appKmMapper.unbindKmWithDuration(km.getId(), now, newEndTime, now);
        return ApiController.buildSuccessResponse("卡密解绑成功", app, null);
    }

    /**
     * 次数卡解绑
     */
    private Map<String, Object> handleSingleUnbind(App app, AppKm km, long now) {
        int amount = km.getAmount() != null ? km.getAmount() : 0;

        // 无限次卡
        if (amount == SINGLE_UNLIMITED) {
            int kmChange = km.getKmChange() != null ? km.getKmChange() : 0;
            int maxKmChange = app.getKmChange() != null ? app.getKmChange() : 0;
            if (kmChange >= maxKmChange) {
                return ApiController.buildErrorResponse(201, "解绑失败,可解绑次数不足", app, null);
            }

            if (km.getKmChangeTime() != null && km.getKmChangeTime() > 0) {
                int kmChangeTime = app.getKmChangeTime() != null ? app.getKmChangeTime() : 0;
                if ((now - km.getKmChangeTime()) < (3600L * kmChangeTime)) {
                    return ApiController.buildErrorResponse(201, "解绑失败,换绑时间不足", app, null);
                }
            }

            appKmMapper.unbindKm(km.getId(), now);
            return ApiController.buildSuccessResponse("卡密解绑成功", app, null);
        }

        // 普通次数卡
        int singleKmChangeNum = app.getSingleKmChangeNum() != null ? app.getSingleKmChangeNum() : 0;
        if (amount < singleKmChangeNum) {
            return ApiController.buildErrorResponse(201, "次数不足", app, null);
        }

        int kmChange = km.getKmChange() != null ? km.getKmChange() : 0;
        int maxKmChange = app.getKmChange() != null ? app.getKmChange() : 0;
        if (kmChange >= maxKmChange) {
            return ApiController.buildErrorResponse(201, "解绑失败,可解绑次数不足", app, null);
        }

        if (km.getKmChangeTime() != null && km.getKmChangeTime() > 0) {
            int kmChangeTime = app.getKmChangeTime() != null ? app.getKmChangeTime() : 0;
            if ((now - km.getKmChangeTime()) < (3600L * kmChangeTime)) {
                return ApiController.buildErrorResponse(201, "解绑失败,换绑时间不足", app, null);
            }
        }

        int newAmount = amount - singleKmChangeNum;
        appKmMapper.unbindKmWithAmount(km.getId(), now, newAmount, now);
        return ApiController.buildSuccessResponse("卡密解绑成功", app, null);
    }
}
