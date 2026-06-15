package com.easycloud.config;

import com.easycloud.interceptor.AdminAuthInterceptor;
import com.easycloud.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Security 和 MVC 拦截器配置
 * <p>
 * 安全架构设计：
 * <ul>
 *   <li>Spring Security 层：禁用 CSRF 和 Session，所有请求放行（permitAll）</li>
 *   <li>Spring MVC Interceptor 层：实际的认证控制由自定义拦截器负责</li>
 * </ul>
 * <p>
 * 拦截器注册：
 * <ul>
 *   <li>AdminAuthInterceptor - 拦截 /api/admin/**，排除 /api/admin/login，验证 JWT Token</li>
 *   <li>UserAuthInterceptor - 拦截 /api/user/**，验证 JWT Token 并解析用户 UID</li>
 * </ul>
 * <p>
 * 公开接口（无需认证）：
 * <ul>
 *   <li>/api/legacy/** - 对外 API，有自有的签名验证机制</li>
 *   <li>/api/pay/{type}/notify - 支付网关回调</li>
 *   <li>/api/captcha/** - 验证码接口</li>
 *   <li>/api/admin/login - 管理员登录</li>
 * </ul>
 * <p>
 * 密码编码器使用 MD5，与原 PHP 系统的 md5(pwd + '!@#%!s!0') 加密方式兼容。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final UserAuthInterceptor userAuthInterceptor;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 安全响应头配置
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(cto -> {})
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                )
                .authorizeHttpRequests(auth -> auth
                        // /api/legacy/** - 对外 API，有自己的签名验证（ApiSignature）
                        .requestMatchers("/api/legacy/**").permitAll()
                        // /api/pay/*/notify - 支付平台回调，无需认证
                        .requestMatchers("/api/pay/*/notify").permitAll()
                        // /api/captcha/** - 验证码等公开接口
                        .requestMatchers("/api/captcha/**").permitAll()
                        // /api/user/** - 用户端接口，自身有 JWT 校验
                        .requestMatchers("/api/user/**").permitAll()
                        // /api/admin/login - 管理员登录接口
                        .requestMatchers("/api/admin/login").permitAll()
                        // /admin/login - 管理后台登录页
                        .requestMatchers("/admin/login").permitAll()
                        // Swagger / Knife4j 文档
                        .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        // 静态资源
                        .requestMatchers("/static/**", "/uploads/**").permitAll()
                        // 其余请求 permitAll - 安全控制由 Spring MVC Interceptor（AdminAuthInterceptor）负责
                        // AdminAuthInterceptor 已对 /api/admin/** 做 JWT 认证拦截，/api/admin/login 被排除
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/user/**", "/api/pay/create")
                .excludePathPatterns("/api/user/login", "/api/user/register");
    }

    /**
     * 密码编码器 - 使用 MD5 兼容原 PHP 系统
     * 原系统密码加密方式: md5(pwd + '!@#%!s!0')
     */
    @Bean
    public MessageDigestPasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("MD5");
    }
}
