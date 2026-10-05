package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.Result;
import com.easycloud.entity.Message;
import com.easycloud.entity.Site;
import com.easycloud.entity.Tixian;
import com.easycloud.entity.UserJk;
import com.easycloud.mapper.MessageMapper;
import com.easycloud.mapper.SiteMapper;
import com.easycloud.mapper.TixianMapper;
import com.easycloud.mapper.UserJkMapper;
import com.easycloud.service.ConfigService;
import com.easycloud.service.MailService;
import com.easycloud.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 系统设置 - 对应 PHP admin/set.php 与 ajax.php 的
 * set / mailtest / optim / repair / api_key / api_ip / get_apijk / site_endtime / transfer / transfer_config
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/setting")
@RequiredArgsConstructor
public class AdminSettingController {

    private static final Set<String> SENSITIVE_KEYS = Set.of("admin_pwd", "admin_user", "db_pwd", "mail_pwd",
            "sms_appkey", "wxpay_key", "qqpay_key", "transfer_key", "api_key", "transfer_pass");
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 反引号（用于表名转义），以 unicode 转义书写避免源码字符问题 */
    private static final String BACKTICK = "\u0060";
    /** 双引号字符 */
    private static final String Q = "\"";

    private final ConfigService configService;
    private final MailService mailService;
    private final RateLimiterService rateLimiterService;
    private final JdbcTemplate jdbcTemplate;
    private final MessageMapper messageMapper;
    private final SiteMapper siteMapper;
    private final TixianMapper tixianMapper;
    private final UserJkMapper userJkMapper;

    @GetMapping
    public Result<?> getSettings() {
        Map<String, String> settings = configService.getAllSettings();
        Map<String, String> masked = new LinkedHashMap<>(settings);
        for (String key : SENSITIVE_KEYS) {
            String value = masked.get(key);
            if (value != null && !value.isEmpty()) {
                masked.put(key, configService.maskKey(value));
            }
        }
        return Result.ok(masked);
    }

    @GetMapping("/{key}")
    public Result<?> getSetting(@PathVariable String key) {
        String value = configService.getSetting(key);
        Map<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("value", SENSITIVE_KEYS.contains(key) && value != null && !value.isEmpty()
                ? configService.maskKey(value) : value);
        return Result.ok(data);
    }

    @PostMapping
    public Result<?> saveSettings(@RequestBody Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            configService.saveSetting(entry.getKey(), entry.getValue());
        }
        return Result.ok("保存成功");
    }

    @PostMapping("/refresh-cache")
    public Result<?> refreshCache() {
        configService.refreshCache();
        return Result.ok("缓存已刷新");
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.fail(400, "新密码至少6位");
        }
        String stored = configService.getSetting("admin_pwd");
        if (!verifyAdminPassword(oldPassword, stored)) {
            return Result.fail(400, "当前密码错误");
        }
        configService.saveSetting("admin_pwd", md5(newPassword));
        return Result.ok("密码修改成功");
    }

    @PostMapping("/change-account")
    public Result<?> changeAccount(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        String newUsername = body.get("username");
        String stored = configService.getSetting("admin_pwd");
        if (!verifyAdminPassword(password, stored)) {
            return Result.fail(400, "当前密码错误");
        }
        if (newUsername == null || newUsername.isBlank()) {
            return Result.fail(400, "请输入新用户名");
        }
        configService.saveSetting("admin_user", newUsername);
        return Result.ok("账号修改成功");
    }

    @GetMapping("/messages")
    public Result<?> getMessages(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Message::getAddtime);
        return Result.ok(messageMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PostMapping("/messages")
    public Result<?> createMessage(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        if (title == null || title.isBlank()) {
            return Result.fail(400, "请输入消息标题");
        }
        Message message = new Message();
        message.setTitle(title);
        message.setType(body.getOrDefault("type", "system"));
        message.setContent(body.get("content"));
        message.setAddtime(LocalDateTime.now());
        messageMapper.insert(message);
        return Result.ok("发布成功");
    }

    @PostMapping("/mail-test")
    public Result<?> mailTest(@RequestBody Map<String, String> body) {
        String to = body.get("to");
        if (to == null || !to.matches("^.+@.+$")) {
            return Result.fail(400, "请输入正确的邮箱地址");
        }
        boolean ok = mailService.sendMailSync(to, "邮件发送测试", "这是一封测试邮件！来自 EasyCloud 系统设置。");
        return ok ? Result.ok("测试邮件已发送") : Result.fail(500, "发送失败");
    }

    @PostMapping("/db-optim")
    public Result<?> dbOptim() {
        return executeTableMaintenance("OPTIMIZE");
    }

    @PostMapping("/db-repair")
    public Result<?> dbRepair() {
        return executeTableMaintenance("REPAIR");
    }

    private Result<?> executeTableMaintenance(String action) {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            int count = 0;
            for (Map<String, Object> row : tables) {
                for (Object value : row.values()) {
                    String table = String.valueOf(value);
                    if (!TABLE_NAME_PATTERN.matcher(table).matches()) {
                        continue;
                    }
                    jdbcTemplate.execute(action + " TABLE " + BACKTICK + sanitizeTableName(table) + BACKTICK);
                    count++;
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("tables", count);
            return Result.ok(action.equals("OPTIMIZE") ? "已成功优化所有数据表" : "已成功修复所有数据表", data);
        } catch (Exception e) {
            log.error("数据库维护失败: {}", e.getMessage());
            return Result.fail(500, "操作失败: " + e.getMessage());
        }
    }

    private String sanitizeTableName(String name) {
        if (!TABLE_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("非法表名: " + name);
        }
        return name;
    }

    @GetMapping("/api-key")
    public Result<?> getApiKey() {
        Map<String, Object> data = new HashMap<>();
        data.put("apiKey", configService.getSetting("api_key"));
        return Result.ok(data);
    }

    @PostMapping("/api-key")
    public Result<?> generateApiKey() {
        if (!rateLimiterService.isAllowed("admin_apikey_reset", 1, 600)) {
            return Result.fail(429, "请勿频繁重置！");
        }
        StringBuilder sb = new StringBuilder(32);
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        configService.saveSetting("api_key", sb.toString());
        return Result.ok("APIKEY重置成功", sb.toString());
    }

    @GetMapping("/api-ip")
    public Result<?> getApiIp() {
        Map<String, Object> data = new HashMap<>();
        data.put("apiIplist", configService.getSetting("api_iplist"));
        return Result.ok(data);
    }

    @PostMapping("/api-ip")
    public Result<?> saveApiIp(@RequestBody Map<String, String> body) {
        String data = body.get("data");
        configService.saveSetting("api_iplist", data == null ? "" : data.trim());
        return Result.ok("设置成功");
    }

    @GetMapping("/sites")
    public Result<?> getSites(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "20") int size) {
        LambdaQueryWrapper<Site> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Site::getId);
        return Result.ok(siteMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PostMapping("/sites/{id}/endtime")
    public Result<?> updateSiteEndtime(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Site site = siteMapper.selectById(id);
        if (site == null) {
            return Result.fail("分站不存在");
        }
        int num;
        try {
            num = Integer.parseInt(String.valueOf(body.get("num")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "续时数量无效");
        }
        int timeType = 0;
        try {
            timeType = Integer.parseInt(orDefault(configService.getSetting("auth_time_type"), "0"));
        } catch (NumberFormatException ignored) {
        }
        LocalDateTime base = site.getEndtime() != null && site.getEndtime().isAfter(LocalDateTime.now())
                ? site.getEndtime() : LocalDateTime.now();
        LocalDateTime endtime;
        if (timeType == 2) {
            endtime = base.plusYears(num);
        } else if (timeType == 1) {
            endtime = base.plusMonths(num);
        } else {
            endtime = base.plusDays(num);
        }
        Site update = new Site();
        update.setId(id);
        update.setEndtime(endtime);
        siteMapper.updateById(update);
        return Result.ok("续时成功");
    }

    /**
     * 接口监控串 - 对应 PHP ajax.php get_apijk
     */
    @GetMapping("/api-jk")
    public Result<?> getApiJk(@RequestParam(required = false) Long proid) {
        if (proid == null || proid <= 0) {
            return Result.fail(400, "请选择您要生成的程序！");
        }
        String apiKey = orDefault(configService.getSetting("api_key"), "");
        String authUrl = orDefault(configService.getSetting("authurl"), "");
        Map<String, Object> data = new HashMap<>();
        data.put("apiJk", authUrl + "api/cloud_api.php?act=cloud_auth&proid=" + proid
                + "&name=授权站点名称&qq=授权QQ&url=授权域名&ip=服务器ip&key=" + apiKey);
        data.put("apisqsJk", authUrl + "api/cloud_api.php?act=cloud_user&proid=" + proid
                + "&power=1&user=登录用户名&pwd=登录密码&qq=联系QQ&email=绑定邮箱&ip=服务器ip&key=" + apiKey);
        data.put("apicgJk", authUrl + "api/cloud_api.php?act=cloud_user&proid=" + proid
                + "&power=2&user=登录用户名&pwd=登录密码&qq=联系QQ&email=绑定邮箱&ip=服务器ip&key=" + apiKey);
        return Result.ok(data);
    }

    /**
     * 用户关联接口监控 - 对应 PHP ajax.php get_user_apijk
     */
    @GetMapping("/api-user-jk")
    public Result<?> getUserApiJk(@RequestParam(required = false) Long proid) {
        if (proid == null || proid <= 0) {
            return Result.fail(400, "请选择您要生成的程序！");
        }
        LambdaQueryWrapper<UserJk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserJk::getAppid, proid).orderByDesc(UserJk::getId);
        return Result.ok(userJkMapper.selectList(wrapper));
    }

    /**
     * 自动转账打款 - 对应 PHP ajax.php transfer（fcypay 代付接口）
     */
    @PostMapping("/transfer")
    public Result<?> transfer(@RequestBody Map<String, Object> body) {
        if (!"1".equals(orDefault(configService.getSetting("user_daifu"), "0"))) {
            return Result.fail(400, "请先在用户设置开启代付接口");
        }
        String transferId = configService.getSetting("transfer_id");
        String transferKey = configService.getSetting("transfer_key");
        String transferCheck = configService.getSetting("transfer_check");
        String transferPass = configService.getSetting("transfer_pass");
        if (isBlank(transferId) || isBlank(transferKey) || isBlank(transferCheck) || isBlank(transferPass)) {
            return Result.fail(400, "请先配置好自动转账接口信息");
        }
        Long id;
        try {
            id = Long.parseLong(String.valueOf(body.get("id")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "参数无效");
        }
        Tixian tixian = tixianMapper.selectOne(new LambdaQueryWrapper<Tixian>()
                .eq(Tixian::getId, id).eq(Tixian::getStatus, Tixian.STATUS_PENDING));
        if (tixian == null) {
            return Result.fail(400, "记录不存在或状态不是待处理！");
        }
        String type;
        if ("1".equals(String.valueOf(tixian.getType()))) {
            type = "3";
        } else if ("0".equals(String.valueOf(tixian.getType()))) {
            type = "1";
        } else {
            type = String.valueOf(tixian.getType());
        }
        TreeMap<String, String> param = new TreeMap<>();
        param.put("api_id", transferId.trim());
        param.put("money", tixian.getRealmoney() == null
                ? tixian.getMoney().toPlainString() : tixian.getRealmoney().toPlainString());
        param.put("payee_type", type);
        param.put("payee_account", tixian.getAccount());
        param.put("payee_name", tixian.getName());
        param.put("realname", transferCheck);
        param.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        param.put("pay_pass", transferPass);
        param.put("sign", generateSign(param, transferKey.trim()));
        try {
            StringBuilder form = new StringBuilder();
            param.forEach((k, v) -> form.append(k).append('=')
                    .append(java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8)).append('&'));
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://api.fcypay.com/transfer"))
                    .timeout(java.time.Duration.ofSeconds(15))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(
                            form.substring(0, form.length() - 1)))
                    .build();
            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(15)).build()
                    .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            String resp = response.body();
            String codeKey = Q + "code" + Q;
            boolean success = resp.contains(codeKey + ":1") || resp.contains(codeKey + ": 1");
            if (success) {
                int updated = tixianMapper.update(null, new LambdaUpdateWrapper<Tixian>()
                        .eq(Tixian::getId, id)
                        .eq(Tixian::getStatus, Tixian.STATUS_PENDING)
                        .set(Tixian::getStatus, Tixian.STATUS_APPROVED)
                        .set(Tixian::getEndtime, LocalDateTime.now()));
                if (updated == 0) {
                    return Result.fail(500, "汇款成功!但是结算记录状态改变失败！");
                }
                return Result.ok("汇款成功");
            }
            String msg = extractJsonField(resp, "msg");
            return Result.fail(500, msg == null ? "对接平台未知错误" : msg);
        } catch (Exception e) {
            log.error("自动转账请求失败: {}", e.getMessage());
            return Result.fail(500, "对接平台请求失败");
        }
    }

    /**
     * 转账配置读取
     */
    @GetMapping("/transfer-config")
    public Result<?> getTransferConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("userDaifu", configService.getSetting("user_daifu"));
        data.put("transferId", configService.getSetting("transfer_id"));
        String key = configService.getSetting("transfer_key");
        data.put("transferKey", key == null ? "" : configService.maskKey(key));
        data.put("transferCheck", configService.getSetting("transfer_check"));
        data.put("configured", !isBlank(configService.getSetting("transfer_pass")));
        return Result.ok(data);
    }

    /**
     * 转账配置保存 - 对应 PHP ajax.php transfer_config
     */
    @PostMapping("/transfer-config")
    public Result<?> saveTransferConfig(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String key = body.get("key");
        String pass = body.get("pass");
        String check = body.get("check");
        if (isBlank(id) || isBlank(key) || isBlank(pass)) {
            return Result.fail(400, "请填写完整");
        }
        if (!"NO_CHECK".equals(check) && !"FORCE_CHECK".equals(check)) {
            return Result.fail(400, "验证选项错误");
        }
        configService.saveSetting("transfer_id", id);
        configService.saveSetting("transfer_key", key);
        configService.saveSetting("transfer_check", check);
        configService.saveSetting("transfer_pass", md5(pass));
        return Result.ok("保存成功");
    }

    private boolean verifyAdminPassword(String input, String stored) {
        if (input == null || stored == null) {
            return false;
        }
        return stored.equals(input) || stored.equals(md5(input));
    }

    /**
     * 代付协议签名：参数字典序 k=v& + key，MD5 小写
     */
    private String generateSign(Map<String, String> params, String key) {
        TreeMap<String, String> sorted = new TreeMap<>(params);
        sorted.remove("sign");
        StringBuilder sb = new StringBuilder();
        sorted.forEach((k, v) -> sb.append(k).append('=').append(v).append('&'));
        sb.append("key=").append(key);
        return md5(sb.toString());
    }

    private String extractJsonField(String json, String key) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        String search = Q + key + Q;
        int k = json.indexOf(search);
        if (k < 0) {
            return null;
        }
        int colon = json.indexOf(':', k + search.length());
        if (colon < 0) {
            return null;
        }
        int start = json.indexOf(Q, colon);
        if (start < 0) {
            return null;
        }
        int end = json.indexOf(Q, start + 1);
        if (end < 0) {
            return null;
        }
        return json.substring(start + 1, end);
    }

    private String md5(String input) {
        return com.easycloud.common.Md5Util.md5(input);
    }

    private String orDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
