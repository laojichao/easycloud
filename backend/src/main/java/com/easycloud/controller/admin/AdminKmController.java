package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.AppKm;
import com.easycloud.mapper.AppKmMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 卡密管理 - 对应 PHP admin/appkmlist.php, addappkm.php
 */
@RestController
@RequestMapping("/api/admin/km")
@RequiredArgsConstructor
public class AdminKmController {

    private final AppKmMapper appKmMapper;

    /** 7种卡密结构的字符池 */
    private static final String CHARS_ALL = "abcdefghijklmnopqrstuvwxyz0***REMOVED***ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARS_LOWER_NUM = "abcdefghijklmnopqrstuvwxyz0***REMOVED***";
    private static final String CHARS_UPPER_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0***REMOVED***";
    private static final String CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARS_NUM = "0***REMOVED***";
    private static final String CHARS_MIXED_CASE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 卡密列表
     */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long appid,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String useStatus) {

        LambdaQueryWrapper<AppKm> wrapper = new LambdaQueryWrapper<>();
        if (appid != null) {
            wrapper.eq(AppKm::getAppid, appid);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(AppKm::getKami, keyword)
                    .or().like(AppKm::getNote, keyword)
                    .or().like(AppKm::getUser, keyword));
        }
        if (StringUtils.hasText(state)) {
            wrapper.eq(AppKm::getState, state);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(AppKm::getType, type);
        }
        // 按使用状态筛选: unused(未使用), used(已使用), expired(已过期)
        if (StringUtils.hasText(useStatus)) {
            long now = System.currentTimeMillis() / 1000;
            switch (useStatus) {
                case "unused":
                    wrapper.and(w -> w.isNull(AppKm::getUseTime).or().eq(AppKm::getUseTime, 0));
                    break;
                case "used":
                    wrapper.isNotNull(AppKm::getUseTime).ne(AppKm::getUseTime, 0);
                    break;
                case "expired":
                    wrapper.and(w -> w.isNotNull(AppKm::getUseTime).ne(AppKm::getUseTime, 0))
                            .and(w -> w.lt(AppKm::getEndTime, String.valueOf(now))
                                    .ne(AppKm::getEndTime, "4102243200"));
                    break;
            }
        }
        wrapper.orderByDesc(AppKm::getId);

        Page<AppKm> result = appKmMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    /**
     * 生成卡密
     */
    @PostMapping("/generate")
    public Result<?> generate(@RequestBody Map<String, Object> body) {
        Long appId = Long.parseLong(body.get("appid").toString());
        String type = (String) body.getOrDefault("type", "code");
        String kmTime = (String) body.getOrDefault("km_time", "day");
        int count = Integer.parseInt(body.getOrDefault("count", "1").toString());
        int length = Integer.parseInt(body.getOrDefault("length", "16").toString());
        String prefix = (String) body.getOrDefault("prefix", "");
        int amount = Integer.parseInt(body.getOrDefault("amount", "1").toString());
        // 结构类型: 1=混合大小写+数字, 2=大写+数字, 3=小写+数字, 4=小写字母, 5=大写字母, 6=纯数字, 7=大小写字母
        int structure = Integer.parseInt(body.getOrDefault("structure", "1").toString());

        if (count <= 0 || count > 10000) {
            return Result.fail("生成数量必须在1-10000之间");
        }

        String charPool = getCharPool(structure);

        List<String> generatedKeys = new ArrayList<>();
        int maxRetries = 10;
        for (int i = 0; i < count; i++) {
            String kami = null;
            // 重试生成唯一卡密
            for (int retry = 0; retry < maxRetries; retry++) {
                String candidate = prefix + generateRandomString(length - prefix.length(), charPool);
                Long existing = appKmMapper.selectCount(
                        new LambdaQueryWrapper<AppKm>()
                                .eq(AppKm::getKami, candidate)
                                .eq(AppKm::getAppid, appId));
                if (existing == 0) {
                    kami = candidate;
                    break;
                }
            }
            if (kami == null) {
                return Result.fail("生成卡密失败：无法生成唯一卡密，请调整长度或前缀");
            }

            AppKm km = new AppKm();
            km.setAppid(appId);
            km.setKami(kami);
            km.setType(type);
            km.setKmTime(kmTime);
            km.setAmount(amount);
            km.setState("y");
            km.setKmChange(0);
            km.setAddtime(LocalDateTime.now());

            appKmMapper.insert(km);
            generatedKeys.add(kami);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("count", generatedKeys.size());
        data.put("keys", generatedKeys);
        return Result.ok("生成成功", data);
    }

    /**
     * 删除卡密
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        appKmMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    /**
     * 切换卡密状态
     */
    @PostMapping("/{id}/toggle")
    public Result<?> toggle(@PathVariable Long id, @RequestBody Map<String, String> body) {
        AppKm km = appKmMapper.selectById(id);
        if (km == null) {
            return Result.fail("卡密不存在");
        }
        String value = body.get("value");
        km.setState(value);
        appKmMapper.updateById(km);
        return Result.ok("切换成功");
    }

    /**
     * 解绑卡密
     */
    @PostMapping("/{id}/unbind")
    public Result<?> unbind(@PathVariable Long id) {
        AppKm km = appKmMapper.selectById(id);
        if (km == null) {
            return Result.fail("卡密不存在");
        }
        // 使用专用mapper方法，记录km_change和km_change_time
        long now = System.currentTimeMillis() / 1000;
        appKmMapper.unbindKm(id, now);
        return Result.ok("解绑成功");
    }

    /**
     * 批量操作
     * action: enable, disable, delete, unbind, add_time, sub_time, export
     */
    @PostMapping("/batch")
    public Result<?> batch(@RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        @SuppressWarnings("unchecked")
        List<Number> ids = (List<Number>) body.get("ids");
        long hours = body.containsKey("hours") ? ((Number) body.get("hours")).longValue() : 0;

        if (action == null) {
            return Result.fail("参数不完整");
        }

        // export 不需要 ids
        if ("export".equals(action)) {
            return handleExport(body);
        }

        if (ids == null || ids.isEmpty()) {
            return Result.fail("参数不完整");
        }

        for (Number idNum : ids) {
            Long id = idNum.longValue();
            AppKm km = appKmMapper.selectById(id);
            if (km == null) continue;

            switch (action) {
                case "enable":
                    km.setState("y");
                    appKmMapper.updateById(km);
                    break;
                case "disable":
                    km.setState("n");
                    appKmMapper.updateById(km);
                    break;
                case "delete":
                    appKmMapper.deleteById(id);
                    break;
                case "unbind":
                    long now = System.currentTimeMillis() / 1000;
                    appKmMapper.unbindKm(id, now);
                    break;
                case "add_time":
                    adjustTime(km, hours);
                    break;
                case "sub_time":
                    adjustTime(km, -hours);
                    break;
            }
        }

        return Result.ok("批量操作成功");
    }

    /**
     * 调整卡密时长
     */
    private void adjustTime(AppKm km, long hours) {
        if (km.getEndTime() == null) return;
        try {
            long endTime = Long.parseLong(km.getEndTime());
            long newEndTime = endTime + (hours * 3600);
            if (newEndTime < 0) newEndTime = 0;
            km.setEndTime(String.valueOf(newEndTime));
            appKmMapper.updateById(km);
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * 导出卡密（对应 PHP appkm_change aid=4/5）
     */
    private Result<?> handleExport(Map<String, Object> body) {
        Long appId = body.containsKey("appid") ? Long.parseLong(body.get("appid").toString()) : null;
        @SuppressWarnings("unchecked")
        List<Number> ids = (List<Number>) body.get("ids");

        LambdaQueryWrapper<AppKm> wrapper = new LambdaQueryWrapper<>();
        if (appId != null) {
            wrapper.eq(AppKm::getAppid, appId);
        }
        if (ids != null && !ids.isEmpty()) {
            wrapper.in(AppKm::getId, ids.stream().map(Number::longValue).toList());
        }
        wrapper.select(AppKm::getKami);
        wrapper.orderByDesc(AppKm::getId);

        List<AppKm> kms = appKmMapper.selectList(wrapper);
        List<String> keys = kms.stream().map(AppKm::getKami).toList();
        return Result.ok(keys);
    }

    /**
     * 清理卡密
     */
    @PostMapping("/clean")
    public Result<?> clean(@RequestBody Map<String, Object> body) {
        Long appId = body.containsKey("appid") ? Long.parseLong(body.get("appid").toString()) : null;
        String type = (String) body.getOrDefault("type", "all");

        LambdaQueryWrapper<AppKm> wrapper = new LambdaQueryWrapper<>();
        if (appId != null) {
            wrapper.eq(AppKm::getAppid, appId);
        }

        switch (type) {
            case "used":
                wrapper.isNotNull(AppKm::getUseTime).ne(AppKm::getUseTime, 0);
                break;
            case "unused":
                wrapper.and(w -> w.isNull(AppKm::getUseTime).or().eq(AppKm::getUseTime, 0));
                break;
            case "expired":
                // 过期卡密：非永久卡且结束时间小于当前时间
                wrapper.lt(AppKm::getEndTime, String.valueOf(System.currentTimeMillis() / 1000))
                        .ne(AppKm::getEndTime, "4102243200");
                break;
            case "all":
            default:
                break;
        }

        int count = appKmMapper.delete(wrapper);
        return Result.ok("清理成功，共删除 " + count + " 条卡密");
    }

    /**
     * 根据结构类型获取字符池（对应 PHP RandomStr1-6 + 默认）
     */
    private String getCharPool(int structure) {
        switch (structure) {
            case 1: return CHARS_ALL;          // 混合大小写+数字
            case 2: return CHARS_UPPER_NUM;    // 大写+数字
            case 3: return CHARS_LOWER_NUM;    // 小写+数字
            case 4: return CHARS_LOWER;        // 小写字母
            case 5: return CHARS_UPPER;        // 大写字母
            case 6: return CHARS_NUM;          // 纯数字
            case 7: return CHARS_MIXED_CASE;   // 大小写字母
            default: return CHARS_ALL;
        }
    }

    private String generateRandomString(int length, String charPool) {
        if (length <= 0) length = 8;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charPool.charAt(RANDOM.nextInt(charPool.length())));
        }
        return sb.toString();
    }
}
