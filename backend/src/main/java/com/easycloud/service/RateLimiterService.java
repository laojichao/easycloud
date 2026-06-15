package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 简易速率限制服务
 * <p>
 * 基于 Redis 的滑动窗口计数器，用于登录/注册等敏感接口的防暴力破解。
 * 同一 IP 在窗口时间内超过最大尝试次数后将被拒绝。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    /** Redis 键前缀 */
    private static final String PREFIX = "easycloud:ratelimit:";

    /**
     * 检查是否超出速率限制
     *
     * @param key      限流键（如 IP 地址或 IP+接口名）
     * @param maxCount 窗口内最大允许次数
     * @param windowSeconds 窗口时间（秒）
     * @return true=允许通过, false=已超限
     */
    public boolean isAllowed(String key, int maxCount, int windowSeconds) {
        String redisKey = PREFIX + key;
        try {
            Long count = stringRedisTemplate.opsForValue().increment(redisKey);
            if (count == null || count == 1) {
                // 首次访问，设置过期时间
                stringRedisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
            }
            if (count > maxCount) {
                log.warn("速率限制触发: key={}, count={}", key, count);
                return false;
            }
            return true;
        } catch (Exception e) {
            // Redis 不可用时放行（降级策略：不因缓存故障阻断正常登录）
            log.warn("速率限制检查失败（Redis 不可用），放行: error={}", e.getMessage());
            return true;
        }
    }

    /**
     * 重置指定键的计数器（登录成功后调用）
     */
    public void reset(String key) {
        try {
            stringRedisTemplate.delete(PREFIX + key);
        } catch (Exception ignored) {
        }
    }
}
