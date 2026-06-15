package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.LogUtil;
import com.easycloud.common.Md5Util;
import com.easycloud.common.Result;
import com.easycloud.entity.Message;
import com.easycloud.entity.Site;
import com.easycloud.mapper.MessageMapper;
import com.easycloud.mapper.SiteMapper;
import com.easycloud.service.ConfigService;
import com.easycloud.service.MailService;
import com.easycloud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统设置控制器
 * <p>
 * 提供系统配置管理、密码修改、消息管理、数据库维护和 API 密钥管理等功能。
 * 敏感配置项（admin_pwd、admin_user、db_pwd、mail_pwd）不允许通过接口读取或修改。
 * <p>
 * 对应原 PHP 文件: admin/set.php（系统设置页面）
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/setting")
@RequiredArgsConstructor
public class AdminSettingController {

    private final ConfigService configService;
    private final MessageMapper messageMapper;
    private final MailService mailService;
    private final JdbcTemplate jdbcTemplate;
    private final SiteMapper siteMapper;
    private final UserService userService;
    private final LogUtil logUtil;

    /** 敏感配置项 - 不允许通过接口读取或修改 */
    private static final Set<String> SENSITIVE_KEYS = Set.of("admin_pwd", "admin_user", "db_pwd", "mail_pwd");

    /**
     * 获取所有设置
     */
    @GetMapping
    public Result<?> getSettings() {
        Map<String, String> settings = configService.getAllSettings();
        // 移除所有敏感信息
        for (String key : SENSITIVE_KEYS) {
            settings.remove(key);
        }
        return Result.ok(settings);
    }

    /**
     * 获取单个设置
     */
    @GetMapping("/{key}")
    public Result<?> getSetting(@PathVariable String key) {
        if (SENSITIVE_KEYS.contains(key)) {
            return Result.fail("不允许读取敏感配置");
        }
        String value = configService.getSetting(key);
        return Result.ok(value);
    }

    /**
     * 保存设置
     */
    @PostMapping
    public Result<?> saveSettings(@RequestBody Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            // 不允许通过此接口修改敏感配置
            if (SENSITIVE_KEYS.contains(entry.getKey())) {
                continue;
            }
            configService.saveSetting(entry.getKey(), entry.getValue());
        }
        // PHP: ajax.php set 操作在保存后清除缓存
        configService.refreshCache();
        return Result.ok("保存成功");
    }

    /**
     * 刷新缓存
     */
    @PostMapping("/refresh-cache")
    public Result<?> refreshCache() {
        configService.refreshCache();
        return Result.ok("缓存刷新成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> body) {
        String oldPwd = body.get("old_password");
        String newPwd = body.get("new_password");

        if (oldPwd == null || oldPwd.isEmpty()) {
            return Result.fail("旧密码不能为空");
        }
        if (newPwd == null || newPwd.length() < 6) {
            return Result.fail("新密码长度不能小于6位");
        }

        String currentPwd = configService.getSetting("admin_pwd");
        // 兼容 PHP 明文和 Java md5
        String encryptedOldPwd = Md5Util.encryptPassword(oldPwd);
        boolean passwordMatch = encryptedOldPwd.equals(currentPwd);
        // 仅当配置的密码不是MD5格式(32位hex)时，才允许明文匹配（兼容初始安装）
        if (!passwordMatch && currentPwd != null && currentPwd.length() != 32) {
            passwordMatch = oldPwd.equals(currentPwd);
        }

        if (!passwordMatch) {
            return Result.fail("旧密码错误");
        }

        String encryptedNewPwd = Md5Util.encryptPassword(newPwd);
        configService.saveSetting("admin_pwd", encryptedNewPwd);
        configService.refreshCache();

        log.info("管理员密码修改成功");
        return Result.ok("密码修改成功");
    }

    /**
     * 修改管理员账号 - 对应 PHP set.php mod=account_n
     */
    @PostMapping("/change-account")
    public Result<?> changeAccount(@RequestBody Map<String, String> body) {
        String newUsername = body.get("username");
        String oldPwd = body.get("password");
        String newPwd = body.get("new_password");

        // 验证当前密码
        if (oldPwd == null || oldPwd.isEmpty()) {
            return Result.fail("请输入当前密码确认操作");
        }
        String currentPwd = configService.getSetting("admin_pwd");
        String encryptedOldPwd = Md5Util.encryptPassword(oldPwd);
        boolean passwordMatch = encryptedOldPwd.equals(currentPwd);
        // 仅当配置的密码不是MD5格式(32位hex)时，才允许明文匹配（兼容初始安装）
        if (!passwordMatch && currentPwd != null && currentPwd.length() != 32) {
            passwordMatch = oldPwd.equals(currentPwd);
        }
        if (!passwordMatch) {
            return Result.fail("密码错误");
        }

        // 修改用户名
        if (newUsername != null && !newUsername.isEmpty()) {
            if (newUsername.length() < 3 || newUsername.length() > 32) {
                return Result.fail("用户名长度应为 3-32 位");
            }
            configService.saveSetting("admin_user", newUsername);
        }

        // 修改密码
        if (newPwd != null && !newPwd.isEmpty()) {
            if (newPwd.length() < 6) {
                return Result.fail("新密码长度不能小于6位");
            }
            configService.saveSetting("admin_pwd", Md5Util.encryptPassword(newPwd));
        }

        configService.refreshCache();
        log.info("管理员账号信息修改: newUsername={}", newUsername != null ? newUsername : "(未修改)");
        return Result.ok("账号信息修改成功");
    }

    /**
     * 获取平台消息列表 - 查询最新平台消息
     *
     * @return 消息列表
     */
    @GetMapping("/messages")
    public Result<List<Message>> getMessages() {
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>().orderByDesc(Message::getId));
        return Result.ok(messages);
    }

    /**
     * 创建平台消息 - 发布平台通知消息
     *
     * @param message 消息实体（title, type, content）
     * @return 操作结果
     */
    @PostMapping("/messages")
    public Result<?> createMessage(@RequestBody Message message) {
        if (message.getTitle() == null || message.getTitle().isBlank()) {
            return Result.fail("消息标题不能为空");
        }
        if (message.getContent() == null || message.getContent().isBlank()) {
            return Result.fail("消息内容不能为空");
        }
        if (message.getType() == null || message.getType().isBlank()) {
            message.setType("system");
        }
        message.setAddtime(LocalDateTime.now());
        messageMapper.insert(message);
        return Result.ok("消息发布成功");
    }

    /**
     * 邮件测试 - 向指定邮箱发送测试邮件
     *
     * @param body 请求体，包含 to（收件人邮箱）
     * @return 发送结果
     */
    @PostMapping("/mail-test")
    public Result<?> mailTest(@RequestBody Map<String, String> body) {
        String to = body.get("to");
        if (to == null || to.isBlank()) {
            return Result.fail("收件人邮箱不能为空");
        }

        boolean success = mailService.sendMailSync(to, "测试邮件", "这是一封测试邮件");
        if (success) {
            return Result.ok("测试邮件发送成功");
        } else {
            return Result.fail("测试邮件发送失败，请检查邮件配置");
        }
    }

    /**
     * 数据库优化 - 对指定表执行 OPTIMIZE TABLE
     * <p>
     * 注意：OPTIMIZE TABLE 在 H2 数据库下不可用，仅适用于 MySQL。
     *
     * @param tables 表名列表
     * @return 操作结果
     */
    @PostMapping("/db-optim")
    public Result<?> dbOptim(@RequestBody List<String> tables) {
        if (tables == null || tables.isEmpty()) {
            return Result.fail("请指定要优化的表");
        }

        StringBuilder failedTables = new StringBuilder();
        for (String table : tables) {
            if (table == null || table.isBlank()) {
                continue;
            }
            try {
                jdbcTemplate.execute("OPTIMIZE TABLE " + sanitizeTableName(table));
            } catch (Exception e) {
                if (failedTables.length() > 0) {
                    failedTables.append(", ");
                }
                failedTables.append(table).append("(").append(e.getMessage()).append(")");
            }
        }

        if (failedTables.length() > 0) {
            return Result.fail("部分表优化失败: " + failedTables);
        }
        return Result.ok("数据库优化完成");
    }

    /**
     * 数据库修复 - 对指定表执行 REPAIR TABLE
     * <p>
     * 注意：REPAIR TABLE 在 H2 数据库下不可用，仅适用于 MySQL MyISAM 引擎。
     *
     * @param tables 表名列表
     * @return 操作结果
     */
    @PostMapping("/db-repair")
    public Result<?> dbRepair(@RequestBody List<String> tables) {
        if (tables == null || tables.isEmpty()) {
            return Result.fail("请指定要修复的表");
        }

        StringBuilder failedTables = new StringBuilder();
        for (String table : tables) {
            if (table == null || table.isBlank()) {
                continue;
            }
            try {
                jdbcTemplate.execute("REPAIR TABLE " + sanitizeTableName(table));
            } catch (Exception e) {
                if (failedTables.length() > 0) {
                    failedTables.append(", ");
                }
                failedTables.append(table).append("(").append(e.getMessage()).append(")");
            }
        }

        if (failedTables.length() > 0) {
            return Result.fail("部分表修复失败: " + failedTables);
        }
        return Result.ok("数据库修复完成");
    }

    /**
     * 生成 API 密钥 - 生成 32 位随机密钥并保存到 yixi_config
     *
     * @return 新生成的 API 密钥
     */
    @PostMapping("/api-key")
    public Result<String> generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24]; // 24 bytes -> 32 chars in Base64
        random.nextBytes(bytes);
        String apiKey = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        // 截取或补足到 32 位
        if (apiKey.length() > 32) {
            apiKey = apiKey.substring(0, 32);
        }

        configService.saveSetting("api_key", apiKey);
        return Result.ok("API 密钥生成成功", apiKey);
    }

    /**
     * 获取 API 密钥 - 从配置中读取当前 API 密钥
     *
     * @return 当前 API 密钥
     */
    @GetMapping("/api-key")
    public Result<String> getApiKey() {
        String apiKey = configService.getSetting("api_key");
        return Result.ok(apiKey != null ? apiKey : "");
    }

    /**
     * 保存 API IP 白名单 - 保存允许调用 API 的 IP 地址列表
     *
     * @param body 请求体，包含 whitelist（IP 白名单，多个用逗号分隔）
     * @return 操作结果
     */
    @PostMapping("/api-ip")
    public Result<?> saveApiIp(@RequestBody Map<String, String> body) {
        String whitelist = body.get("whitelist");
        if (whitelist == null) {
            whitelist = "";
        }
        configService.saveSetting("api_ip_whitelist", whitelist);
        return Result.ok("API IP 白名单保存成功");
    }

    /**
     * 获取 API IP 白名单 - 读取当前允许调用 API 的 IP 地址列表
     *
     * @return IP 白名单字符串
     */
    @GetMapping("/api-ip")
    public Result<String> getApiIp() {
        String whitelist = configService.getSetting("api_ip_whitelist");
        return Result.ok(whitelist != null ? whitelist : "");
    }

    // ==================== 站点管理 ====================

    /**
     * 获取所有分站信息
     * <p>
     * 查询 yixi_site 表中的所有分站记录，返回分站列表。
     *
     * @return 分站列表
     */
    @GetMapping("/sites")
    public Result<?> getSites() {
        List<Site> sites = siteMapper.selectList(
                new LambdaQueryWrapper<Site>().orderByDesc(Site::getId));
        return Result.ok(sites);
    }

    /**
     * 更新分站到期时间
     * <p>
     * 管理员手动调整指定分站的到期时间，用于续费或延期操作。
     *
     * @param id   分站 ID
     * @param body 请求体，包含 endtime（到期时间，格式：yyyy-MM-dd HH:mm:ss）
     * @return 操作结果
     */
    @PostMapping("/sites/{id}/endtime")
    public Result<?> updateSiteEndtime(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String endtime = body.get("endtime");
        if (endtime == null || endtime.isEmpty()) {
            return Result.fail("到期时间不能为空");
        }

        Site site = siteMapper.selectById(id);
        if (site == null) {
            return Result.fail("分站不存在");
        }

        try {
            site.setEndtime(LocalDateTime.parse(endtime.replace(" ", "T")));
            siteMapper.updateById(site);
            return Result.ok("到期时间更新成功");
        } catch (Exception e) {
            return Result.fail("时间格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }
    }

    // ==================== API 接口列表 ====================

    /**
     * 获取系统支持的所有 API 接口列表
     * <p>
     * 返回 EasyCloud 验证系统对外提供的所有 API 接口信息，
     * 包括接口名称和功能描述，供管理后台展示和前端文档使用。
     *
     * @return API 接口列表 [{name, desc}, ...]
     */
    @GetMapping("/api-jk")
    public Result<?> getApiJk() {
        List<Map<String, String>> apiList = new ArrayList<>();

        Map<String, String> ini = new LinkedHashMap<>();
        ini.put("name", "ini");
        ini.put("desc", "获取应用配置（版本号、更新信息、调用次数等）");
        apiList.add(ini);

        Map<String, String> notice = new LinkedHashMap<>();
        notice.put("name", "notice");
        notice.put("desc", "获取应用公告内容");
        apiList.add(notice);

        Map<String, String> getfile = new LinkedHashMap<>();
        getfile.put("name", "getfile");
        getfile.put("desc", "获取应用下的文件下载列表");
        apiList.add(getfile);

        Map<String, String> kmlogon = new LinkedHashMap<>();
        kmlogon.put("name", "kmlogon");
        kmlogon.put("desc", "卡密登录验证（支持时长卡和次数卡）");
        apiList.add(kmlogon);

        Map<String, String> kmunmachine = new LinkedHashMap<>();
        kmunmachine.put("name", "kmunmachine");
        kmunmachine.put("desc", "卡密解绑（解除卡密与设备的绑定关系）");
        apiList.add(kmunmachine);

        return Result.ok(apiList);
    }

    // ==================== 余额转账 ====================

    /**
     * 管理员手动转账
     * <p>
     * 管理员向指定用户的手动转账操作，可增加或减少用户余额。
     * 操作完成后记录日志，便于财务审计。
     *
     * @param body 请求体，包含 uid（用户ID）、amount（转账金额，正数增加负数减少）、remark（备注）
     * @return 操作结果
     */
    @PostMapping("/transfer")
    public Result<?> transfer(@RequestBody Map<String, Object> body) {
        Object uidObj = body.get("uid");
        Object amountObj = body.get("amount");
        String remark = (String) body.getOrDefault("remark", "");

        if (uidObj == null) {
            return Result.fail("用户 ID 不能为空");
        }
        if (amountObj == null) {
            return Result.fail("转账金额不能为空");
        }

        Long uid = Long.parseLong(uidObj.toString());
        BigDecimal amount = new BigDecimal(amountObj.toString());

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return Result.fail("转账金额不能为 0");
        }

        try {
            userService.updateRmb(uid, amount);

            // 记录转账日志
            String logData = String.format("管理员转账: 金额=%s, 备注=%s", amount.toPlainString(), remark);
            logUtil.log(uid.toString(), "transfer", logData, "");

            return Result.ok("转账成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取自动转账配置
     * <p>
     * 读取系统配置中的自动转账相关参数，包括：
     * <ul>
     *   <li>transfer_enable：是否启用自动转账</li>
     *   <li>transfer_rate：转账费率（百分比）</li>
     *   <li>transfer_min：最低转账金额</li>
     *   <li>transfer_max：最高转账金额</li>
     * </ul>
     *
     * @return 自动转账配置
     */
    @GetMapping("/transfer-config")
    public Result<Map<String, String>> getTransferConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("transfer_enable", configService.getSetting("transfer_enable"));
        config.put("transfer_rate", configService.getSetting("transfer_rate"));
        config.put("transfer_min", configService.getSetting("transfer_min"));
        config.put("transfer_max", configService.getSetting("transfer_max"));
        return Result.ok(config);
    }

    /**
     * 保存自动转账配置
     * <p>
     * 保存自动转账相关参数到系统配置。
     *
     * @param body 请求体，包含 transfer_enable、transfer_rate、transfer_min、transfer_max
     * @return 操作结果
     */
    @PostMapping("/transfer-config")
    public Result<?> saveTransferConfig(@RequestBody Map<String, String> body) {
        String[] keys = {"transfer_enable", "transfer_rate", "transfer_min", "transfer_max"};
        for (String key : keys) {
            if (body.containsKey(key)) {
                configService.saveSetting(key, body.get(key));
            }
        }
        configService.refreshCache();
        return Result.ok("自动转账配置保存成功");
    }

    /** 允许维护的白名单表名 */
    private static final Set<String> ALLOWED_TABLES = Set.of(
            "yixi_apps", "yixi_appkm", "yixi_appfile", "yixi_config", "yixi_log",
            "yixi_cache", "yixi_sig", "yixi_payment_order", "yixi_qiandao",
            "yixi_workorder", "yixi_invitelog", "yixi_points", "yixi_tixian",
            "yixi_user", "yixi_appuser", "yixi_userjk", "yixi_site", "yixi_program", "yixi_message"
    );

    /**
     * 表名安全校验 - 白名单校验，防止 SQL 注入
     *
     * @param tableName 原始表名
     * @return 校验后的表名
     */
    private String sanitizeTableName(String tableName) {
        String sanitized = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        if (sanitized.isEmpty() || !ALLOWED_TABLES.contains(sanitized)) {
            throw new RuntimeException("不允许操作的表: " + tableName);
        }
        return sanitized;
    }
}
