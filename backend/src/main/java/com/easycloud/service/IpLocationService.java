package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP 地理定位服务 - 对应 PHP whois.pconline.com.cn IP 查询
 * <p>
 * 支持多个 API 源：
 * 1. pconline.com.cn（默认，国内精准）
 * 2. ip-api.com（备用，国际）
 * <p>
 * 结果缓存到 Redis，24 小时过期。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpLocationService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String CACHE_PREFIX = "easycloud:ip_location:";
    private static final long CACHE_EXPIRE_HOURS = 24;
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    /** 复用 HttpClient 实例，避免每次请求创建新连接 */
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    /**
     * 获取 IP 归属地（城市级别）
     * 优先从 Redis 缓存读取，未命中则调用外部 API。
     *
     * @param ip IP 地址
     * @return 城市名称，查询失败返回 "未知"
     */
    public String getCity(String ip) {
        if (ip == null || ip.isBlank()) {
            return "未知";
        }

        // 处理本地 IP
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equals(ip)) {
            return "本机";
        }

        // 1. 查 Redis 缓存
        String cacheKey = CACHE_PREFIX + ip;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 2. 调用外部 API（按优先级尝试）
        String city = queryFromPconline(ip);
        if (city == null) {
            city = queryFromIpApi(ip);
        }
        if (city == null) {
            city = "未知";
        }

        // 3. 写入 Redis 缓存（24 小时）
        stringRedisTemplate.opsForValue().set(cacheKey, city, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        return city;
    }

    /**
     * 通过 pconline.com.cn 查询 IP 归属地
     * 对应 PHP: http://whois.pconline.com.cn/ipJson.jsp
     *
     * @param ip IP 地址
     * @return 城市名称，失败返回 null
     */
    private String queryFromPconline(String ip) {
        try {
            String url = "http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET()
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // 响应为 GBK 编码 JSON，body 已被 HttpClient 以 UTF-8 处理
                // 尝试提取 addr 字段
                String body = response.body();
                String city = extractJsonField(body, "addr");
                if (city != null && !city.isBlank()) {
                    log.info("pconline IP定位成功: ip={}, city={}", ip, city);
                    return city;
                }
            }
        } catch (Exception e) {
            log.warn("pconline IP定位失败: ip={}, error={}", ip, e.getMessage());
        }
        return null;
    }

    /**
     * 通过 ip-api.com 查询 IP 归属地（备用）
     *
     * @param ip IP 地址
     * @return 城市名称，失败返回 null
     */
    private String queryFromIpApi(String ip) {
        try {
            String url = "http://ip-api.com/json/" + ip + "?lang=zh-CN&fields=status,country,regionName,city";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                String status = extractJsonField(body, "status");
                if ("success".equals(status)) {
                    String country = extractJsonField(body, "country");
                    String regionName = extractJsonField(body, "regionName");
                    String city = extractJsonField(body, "city");
                    String location = (country != null ? country : "")
                            + (regionName != null ? regionName : "")
                            + (city != null ? city : "");
                    if (!location.isBlank()) {
                        log.info("ip-api.com IP定位成功: ip={}, location={}", ip, location);
                        return location;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("ip-api.com IP定位失败: ip={}, error={}", ip, e.getMessage());
        }
        return null;
    }

    /**
     * 简单提取 JSON 字段值（避免引入额外 JSON 依赖）
     * 匹配 "key":"value" 格式
     */
    private String extractJsonField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
