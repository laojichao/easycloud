package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.entity.SysConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import com.easycloud.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private static final String REDIS_KEY = "easycloud:config";

    private final SysConfigMapper sysConfigMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取单个配置项。
     * 优先从 Redis Hash 读取；未命中则查 DB 并回写 Redis。
     */
    public String getSetting(String key) {
        // 1. 先查 Redis（Redis 不可用时降级为直查 DB）
        try {
            Object val = stringRedisTemplate.opsForHash().get(REDIS_KEY, key);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception e) {
            log.debug("Redis 读取失败，降级查库: {}", e.getMessage());
        }

        // 2. 查 DB
        SysConfig config = sysConfigMapper.selectById(key);
        if (config == null) {
            return null;
        }

        // 3. 尽力回写 Redis
        try {
            stringRedisTemplate.opsForHash().put(REDIS_KEY, key, config.getV());
        } catch (Exception ignored) {
        }
        return config.getV();
    }

    /**
     * 保存/更新单个配置项。
     * 写 DB（saveOrUpdate 语义），然后更新 Redis。
     */
    public void saveSetting(String key, String value) {
        SysConfig config = new SysConfig();
        config.setK(key);
        config.setV(value);
        if (sysConfigMapper.selectById(key) == null) {
            sysConfigMapper.insert(config);
        } else {
            sysConfigMapper.updateById(config);
        }

        // 同步更新 Redis（失败不影响落库）
        try {
            stringRedisTemplate.opsForHash().put(REDIS_KEY, key, value);
        } catch (Exception e) {
            log.warn("Redis 写入失败: {}", e.getMessage());
        }
    }

    /**
     * 获取全部配置项。
     * 优先从 Redis 读取；Redis 为空则查 DB 全表并回写 Redis。
     */
    public Map<String, String> getAllSettings() {
        // 1. 先查 Redis
        Map<Object, Object> redisEntries = null;
        try {
            redisEntries = stringRedisTemplate.opsForHash().entries(REDIS_KEY);
        } catch (Exception e) {
            log.debug("Redis 读取失败，降级查库: {}", e.getMessage());
        }
        if (redisEntries != null && !redisEntries.isEmpty()) {
            Map<String, String> result = new HashMap<>(redisEntries.size());
            redisEntries.forEach((k, v) -> result.put(k.toString(), v.toString()));
            return result;
        }

        // 2. Redis 为空，查 DB 全表
        List<SysConfig> list = sysConfigMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, String> result = new HashMap<>(list.size());
        for (SysConfig c : list) {
            result.put(c.getK(), c.getV());
        }

        // 3. 尽力回写 Redis
        try {
            stringRedisTemplate.opsForHash().putAll(REDIS_KEY, result);
        } catch (Exception ignored) {
        }

        return result;
    }

    /**
     * 刷新缓存：从 DB 加载全部配置写入 Redis。
     */
    public void refreshCache() {
        List<SysConfig> list = sysConfigMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, String> redisMap = new HashMap<>(list.size());
        for (SysConfig c : list) {
            redisMap.put(c.getK(), c.getV());
        }

        // 先清除旧缓存，再写入（Redis 不可用时仅提示）
        try {
            stringRedisTemplate.delete(REDIS_KEY);
            if (!redisMap.isEmpty()) {
                stringRedisTemplate.opsForHash().putAll(REDIS_KEY, redisMap);
            }
        } catch (Exception e) {
            log.warn("Redis 缓存刷新失败: {}", e.getMessage());
        }
    }

    /**
     * 敏感配置脱敏：保留首尾各 4 位，中间以 **** 代替
     */
    public String maskKey(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}
