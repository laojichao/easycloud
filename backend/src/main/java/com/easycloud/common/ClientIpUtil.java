package com.easycloud.common;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端 IP 地址解析工具类
 * <p>
 * 从 HTTP 请求中提取真实客户端 IP 地址，支持反向代理环境。
 * 依次检查以下 Header（优先级从高到低）：
 * <ol>
 *   <li>X-Forwarded-For（取第一个非内网 IP）</li>
 *   <li>X-Real-IP</li>
 *   <li>CF-Connecting-IP（Cloudflare）</li>
 *   <li>Remote-Addr（兜底）</li>
 * </ol>
 * <p>
 * 此工具类替代了之前分散在 UserController 和 KmlogonHandler 中的重复实现。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
public final class ClientIpUtil {

    private ClientIpUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 从请求中获取客户端真实 IP 地址
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 1. X-Forwarded-For（可能包含多个IP，取第一个非内网IP）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            for (String part : ip.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.startsWith("10.")
                        && !trimmed.startsWith("172.16.")
                        && !trimmed.startsWith("192.168.")) {
                    return trimmed;
                }
            }
        }

        // 2. X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 3. CF-Connecting-IP (Cloudflare)
        ip = request.getHeader("CF-Connecting-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 4. Remote-Addr（兜底）
        return request.getRemoteAddr();
    }
}
