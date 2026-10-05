package com.easycloud.config;

import com.easycloud.interceptor.AdminAuthInterceptor;
import com.easycloud.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .contentTypeOptions(content -> {})
                        .httpStrictTransportSecurity(hsts -> {}))
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理端：除登录外全部拦截
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
        // 用户端：登录/注册放行
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/user/**")
                .excludePathPatterns("/api/user/login", "/api/user/register");
        // 支付下单/查单同样需要用户会话；仅异步回调放行
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/pay/**")
                .excludePathPatterns("/api/pay/wxpay/notify", "/api/pay/qqpay/notify");
    }

    /**
     * 密码编码器 - BCrypt；历史 MD5/明文密码在登录时兼容校验并迁移
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
