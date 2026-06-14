package com.easycloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.entity.SysConfig;
import com.easycloud.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 1. 先查 Redis
        Object val = stringRedisTemplate.opsForHash().get(REDIS_KEY, key);
        if (val != null) {
            return val.toString();
        }

        // 2. Redis 未命中，查 DB
        SysConfig config = sysConfigMapper.selectById(key);
        if (config == null) {
            return null;
        }

        // 3. 回写 Redis
        stringRedisTemplate.opsForHash().put(REDIS_KEY, key, config.getV());
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
        sysConfigMapper.insertOrUpdate(config);

        // 同步更新 Redis
        stringRedisTemplate.opsForHash().put(REDIS_KEY, key, value);
    }

    /**
     * 获取全部配置项。
     * 优先从 Redis 读取；Redis 为空则查 DB 全表并回写 Redis。
     */
    public Map<String, String> getAllSettings() {
        // 1. 先查 Redis
        Map<Object, Object> redisEntries = stringRedisTemplate.opsForHash().entries(REDIS_KEY);
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

        // 3. 回写 Redis
        Map<String, String> redisMap = new HashMap<>(result);
        stringRedisTemplate.opsForHash().putAll(REDIS_KEY, redisMap);

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

        // 先清除旧缓存，再写入
        stringRedisTemplate.delete(REDIS_KEY);
        if (!redisMap.isEmpty()) {
            stringRedisTemplate.opsForHash().putAll(REDIS_KEY, redisMap);
        }
    }
}
