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
 * 用户端认证拦截器：校验 user 角色 JWT，注入 userId
 */
@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ") || !jwtUtil.validateUserToken(token.substring(7))) {
            writeError(response, 401, "未登录或Token无效");
            return false;
        }
        String uid = jwtUtil.getUsernameFromToken(token.substring(7));
        request.setAttribute("userId", Long.valueOf(uid));
        return true;
    }

    private void writeError(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code, msg)));
    }
}
