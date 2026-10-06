package com.easycloud.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：管理员（subject=用户名）与用户（subject=uid）双角色令牌
 */
@Slf4j
@Component
public class JwtUtil {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    private static final String CLAIM_ROLE = "role";

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration; // 默认24小时

    /** 未配置 jwt.secret 时（开发环境）使用的进程内临时密钥，重启即失效 */
    private volatile SecretKey ephemeralKey;

    private SecretKey getSigningKey() {
        if (secret == null || secret.isBlank()) {
            if (ephemeralKey == null) {
                synchronized (this) {
                    if (ephemeralKey == null) {
                        ephemeralKey = Jwts.SIG.HS256.key().build();
                        log.warn("未配置 jwt.secret，已启用进程内临时随机密钥（仅开发用，重启后旧 Token 失效；生产必须设置 JWT_SECRET）");
                    }
                }
            }
            return ephemeralKey;
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAdminToken(String username) {
        return buildToken(username, ROLE_ADMIN);
    }

    public String generateUserToken(Long uid) {
        return buildToken(String.valueOf(uid), ROLE_USER);
    }

    public String generateToken(String username) {
        return buildToken(username, ROLE_ADMIN);
    }

    private String buildToken(String subject, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(subject)
                .claim(CLAIM_ROLE, role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        Object role = claims.get(CLAIM_ROLE);
        return role == null ? ROLE_ADMIN : role.toString();
    }

    public boolean validateAdminToken(String token) {
        return validateWithRole(token, ROLE_ADMIN);
    }

    public boolean validateUserToken(String token) {
        return validateWithRole(token, ROLE_USER);
    }

    private boolean validateWithRole(String token, String expectedRole) {
        try {
            Claims claims = parseToken(token);
            if (claims.getExpiration().before(new Date())) {
                return false;
            }
            Object role = claims.get(CLAIM_ROLE);
            return role == null ? ROLE_ADMIN.equals(expectedRole) : expectedRole.equals(role.toString());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            return !parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
