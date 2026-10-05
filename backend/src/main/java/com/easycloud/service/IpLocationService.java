package com.easycloud.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP 归属地查询 - 对应 PHP get_ip_city()
 * 优先 pconline（GBK 返回），失败退回 ip-api.com，结果 Redis 缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpLocationService {

    private static final String CACHE_PREFIX = "easycloud:iploc:";
    private static final long CACHE_EXPIRE_HOURS = 72;
    private static final Duration TIMEOUT = Duration.ofSeconds(3);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private static final Pattern IP_PATTERN = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");

    private final StringRedisTemplate stringRedisTemplate;

    public String getCity(String ip) {
        if (!StringUtils.hasText(ip) || !IP_PATTERN.matcher(ip).matches()) {
            return "未知";
        }
        if (ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("127.")
                || ip.startsWith("172.16.") || ip.startsWith("172.17.") || ip.startsWith("172.18.")
                || ip.startsWith("172.19.") || ip.startsWith("172.2") || ip.startsWith("172.30.")
                || ip.startsWith("172.31.")) {
            return "内网IP";
        }
        try {
            String cached = stringRedisTemplate.opsForValue().get(CACHE_PREFIX + ip);
            if (StringUtils.hasText(cached)) {
                return cached;
            }
        } catch (Exception ignored) {
        }
        String location = queryFromPconline(ip);
        if (!StringUtils.hasText(location)) {
            location = queryFromIpApi(ip);
        }
        if (!StringUtils.hasText(location)) {
            location = "未知";
        }
        try {
            stringRedisTemplate.opsForValue().set(CACHE_PREFIX + ip, location, Duration.ofHours(CACHE_EXPIRE_HOURS));
        } catch (Exception ignored) {
        }
        return location;
    }

    private String queryFromPconline(String ip) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=" + ip))
                    .timeout(TIMEOUT)
                    .header("User-Agent", "Mozilla/5.0")
                    .GET().build();
            HttpResponse<byte[]> resp = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
            String body = new String(resp.body(), Charset.forName("GBK"));
            String pro = extractJsonField(body, "pro");
            String city = extractJsonField(body, "city");
            String location = (pro == null ? "" : pro) + (city == null ? "" : city);
            return location.isBlank() ? null : location;
        } catch (Exception e) {
            log.debug("pconline 归属地查询失败 {}: {}", ip, e.getMessage());
            return null;
        }
    }

    private String queryFromIpApi(String ip) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/" + ip + "?lang=zh-CN"))
                    .timeout(TIMEOUT)
                    .GET().build();
            HttpResponse<String> resp = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String region = extractJsonField(resp.body(), "regionName");
            String city = extractJsonField(resp.body(), "city");
            String location = (region == null ? "" : region) + (city == null ? "" : city);
            return location.isBlank() ? null : location;
        } catch (Exception e) {
            log.debug("ip-api 归属地查询失败 {}: {}", ip, e.getMessage());
            return null;
        }
    }

    private String extractJsonField(String json, String key) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        String search = '"' + key + '"';
        int k = json.indexOf(search);
        if (k < 0) {
            return null;
        }
        int colon = json.indexOf(':', k + search.length());
        if (colon < 0) {
            return null;
        }
        int start = json.indexOf('"', colon);
        if (start < 0) {
            return null;
        }
        int end = json.indexOf('"', start + 1);
        if (end < 0) {
            return null;
        }
        return json.substring(start + 1, end);
    }
}
