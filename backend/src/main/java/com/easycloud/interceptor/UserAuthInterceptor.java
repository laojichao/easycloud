package com.easycloud.interceptor;

import com.easycloud.common.JwtUtil;
import com.easycloud.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户端认证拦截器
 * <p>
 * 拦截所有 /api/user/** 请求，验证 JWT Token 的有效性并解析用户 UID。
 * 验证通过后将用户 UID（Long 类型）注入请求属性 "uid"，供下游 Controller 使用。
 * <p>
 * Token 格式：Authorization: Bearer {token}
 * Token 的 subject 字段存储用户 UID 的字符串形式。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            writeError(response, 401, "未登录或Token无效");
            return false;
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            writeError(response, 401, "Token已过期或无效");
            return false;
        }

        // 用户端 Token subject 存储的是 uid 字符串
        String uidStr = jwtUtil.getUsernameFromToken(token);
        try {
            Long uid = Long.parseLong(uidStr);
            request.setAttribute("uid", uid);
        } catch (NumberFormatException e) {
            writeError(response, 401, "Token格式无效");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code, msg)));
    }
}
