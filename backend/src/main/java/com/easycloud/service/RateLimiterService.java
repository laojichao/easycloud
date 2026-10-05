package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流器：Redis INCR+EXPIRE；Redis 不可用时退化为本地计数窗口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private static final String PREFIX = "easycloud:ratelimit:";
    private static final int MAX_LOCAL_KEYS = 10000;

    private final StringRedisTemplate stringRedisTemplate;
    private final ConcurrentHashMap<String, long[]> localCounters = new ConcurrentHashMap<>();

    /**
     * @param key           限流键（含业务前缀与客户端标识）
     * @param limit         窗口内最大次数
     * @param windowSeconds 窗口长度（秒）
     */
    public boolean isAllowed(String key, int limit, int windowSeconds) {
        try {
            String redisKey = PREFIX + key;
            Long count = stringRedisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
            }
            return count == null || count <= limit;
        } catch (Exception e) {
            log.debug("Redis 限流不可用，使用本地限流: {}", e.getMessage());
            return localIsAllowed(key, limit, windowSeconds);
        }
    }

    private boolean localIsAllowed(String key, int limit, int windowSeconds) {
        if (localCounters.size() > MAX_LOCAL_KEYS) {
            localCounters.clear();
        }
        long now = System.currentTimeMillis();
        long[] slot = localCounters.compute(key, (k, v) -> {
            if (v == null || now - v[0] > windowSeconds * 1000L) {
                return new long[]{now, 1};
            }
            v[1]++;
            return v;
        });
        return slot[1] <= limit;
    }

    public void reset(String key) {
        try {
            stringRedisTemplate.delete(PREFIX + key);
        } catch (Exception ignored) {
        }
        localCounters.remove(key);
    }
}
