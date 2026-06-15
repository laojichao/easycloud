package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.entity.SysConfig;
import com.easycloud.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统配置服务
 * <p>
 * 负责系统配置项的读写和缓存管理，采用 Redis Hash 缓存 + MySQL 持久化的二级存储策略。
 * 所有配置项以键值对（K-V）形式存储，支持单个读写和批量读取。
 * <p>
 * 缓存策略：
 * <ul>
 *   <li>读取时优先从 Redis Hash（easycloud:config）中获取</li>
 *   <li>Redis 未命中时查 MySQL 并回写 Redis</li>
 *   <li>写入时同时更新 MySQL 和 Redis</li>
 * </ul>
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private static final String REDIS_KEY = "easycloud:config";

    /** 敏感配置键名 - 日志中需要脱敏 */
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "admin_pwd", "admin_user", "db_pwd", "mail_pwd",
            "wxpay_key", "qqpay_key", "sms_appkey", "api_key",
            "access_token", "mi_rsa_private_key"
    );

    /**
     * 日志脱敏：敏感键名只保留首尾字符，中间用 *** 替代
     */
    private String maskKey(String key) {
        if (key == null) return "null";
        if (SENSITIVE_KEYS.contains(key)) return key.substring(0, 2) + "***";
        return key;
    }

    private final SysConfigMapper sysConfigMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取单个配置项。
     * 优先从 Redis Hash 读取；未命中则查 DB 并回写 Redis。
     * Redis 不可用时降级为直接查 DB。
     */
    public String getSetting(String key) {
        // 1. 先查 Redis（Redis 不可用时降级到 DB）
        try {
            Object val = stringRedisTemplate.opsForHash().get(REDIS_KEY, key);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception e) {
            log.warn("Redis 读取配置失败，降级查 DB: key={}, error={}", maskKey(key), e.getMessage());
        }

        // 2. 查 DB
        SysConfig config = sysConfigMapper.selectById(key);
        if (config == null) {
            return null;
        }

        // 3. 回写 Redis（失败不影响返回值）
        try {
            stringRedisTemplate.opsForHash().put(REDIS_KEY, key, config.getV());
        } catch (Exception e) {
            log.warn("Redis 回写配置失败: key={}, error={}", maskKey(key), e.getMessage());
        }
        return config.getV();
    }

    /**
     * 保存/更新单个配置项。
     * 写 DB（saveOrUpdate 语义），然后更新 Redis。
     * Redis 不可用时仅写 DB，不影响核心功能。
     */
    public void saveSetting(String key, String value) {
        // 先查后写，避免并发下的竞态条件
        SysConfig existing = sysConfigMapper.selectById(key);
        if (existing != null) {
            existing.setV(value);
            sysConfigMapper.updateById(existing);
        } else {
            SysConfig config = new SysConfig();
            config.setK(key);
            config.setV(value);
            try {
                sysConfigMapper.insert(config);
            } catch (Exception e) {
                // 并发场景下可能因唯一约束冲突，此时转为更新
                log.debug("配置插入冲突，转为更新: key={}", maskKey(key));
                SysConfig fallback = new SysConfig();
                fallback.setK(key);
                fallback.setV(value);
                sysConfigMapper.updateById(fallback);
            }
        }

        // 同步更新 Redis（失败不影响 DB 写入）
        try {
            stringRedisTemplate.opsForHash().put(REDIS_KEY, key, value);
        } catch (Exception e) {
            log.warn("Redis 写入配置失败: key={}, error={}", maskKey(key), e.getMessage());
        }
    }

    /**
     * 获取全部配置项。
     * 优先从 Redis 读取；Redis 为空或不可用则查 DB 全表并回写 Redis。
     */
    public Map<String, String> getAllSettings() {
        // 1. 先查 Redis
        try {
            Map<Object, Object> redisEntries = stringRedisTemplate.opsForHash().entries(REDIS_KEY);
            if (redisEntries != null && !redisEntries.isEmpty()) {
                Map<String, String> result = new HashMap<>(redisEntries.size());
                redisEntries.forEach((k, v) -> result.put(k.toString(), v.toString()));
                return result;
            }
        } catch (Exception e) {
            log.warn("Redis 读取全部配置失败，降级查 DB: error={}", e.getMessage());
        }

        // 2. 查 DB 全表
        List<SysConfig> list = sysConfigMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, String> result = new HashMap<>(list.size());
        for (SysConfig c : list) {
            result.put(c.getK(), c.getV());
        }

        // 3. 回写 Redis（失败不影响返回值）
        try {
            Map<String, String> redisMap = new HashMap<>(result);
            stringRedisTemplate.opsForHash().putAll(REDIS_KEY, redisMap);
        } catch (Exception e) {
            log.warn("Redis 回写全部配置失败: error={}", e.getMessage());
        }

        return result;
    }

    /**
     * 刷新缓存：从 DB 加载全部配置写入 Redis。
     * Redis 不可用时仅记录日志，不影响功能。
     */
    public void refreshCache() {
        List<SysConfig> list = sysConfigMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, String> redisMap = new HashMap<>(list.size());
        for (SysConfig c : list) {
            redisMap.put(c.getK(), c.getV());
        }

        try {
            // 先清除旧缓存，再写入
            stringRedisTemplate.delete(REDIS_KEY);
            if (!redisMap.isEmpty()) {
                stringRedisTemplate.opsForHash().putAll(REDIS_KEY, redisMap);
            }
        } catch (Exception e) {
            log.warn("Redis 刷新缓存失败: error={}", e.getMessage());
        }
    }
}
