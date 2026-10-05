package com.easycloud.common;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端真实 IP 解析 - 对应 PHP real_ip()：按代理头取第一个公网地址
 */
public final class ClientIpUtil {

    private ClientIpUtil() {
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (isForwardedCandidate(xff)) {
            for (String part : xff.split(",")) {
                String ip = part.trim();
                if (isForwardedCandidate(ip) && !isInternalAddress(ip)) {
                    return ip;
                }
            }
        }
        String[] headers = {"X-Real-IP", "HTTP_CF_CONNECTING_IP", "HTTP_CLIENT_IP"};
        for (String h : headers) {
            String ip = request.getHeader(h);
            if (isForwardedCandidate(ip) && !isInternalAddress(ip)) {
                return ip;
            }
        }
        String remote = request.getRemoteAddr();
        return remote == null ? "unknown" : remote;
    }

    private static boolean isForwardedCandidate(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        return ip.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}$") || ip.contains(":");
    }

    private static boolean isInternalAddress(String ip) {
        if (ip == null) {
            return true;
        }
        return ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("127.")
                || ip.startsWith("169.254.")
                || ip.startsWith("0.0.0.0")
                || ip.startsWith("172.16.") || ip.startsWith("172.17.")
                || ip.startsWith("172.18.") || ip.startsWith("172.19.")
                || ip.startsWith("172.2") || ip.startsWith("172.30.") || ip.startsWith("172.31.")
                || ip.startsWith("fc00:") || ip.startsWith("fe80:") || ip.equals("::1");
    }
}
