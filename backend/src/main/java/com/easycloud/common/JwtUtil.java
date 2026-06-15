package com.easycloud.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌工具类
 * <p>
 * 负责管理员后台和用户端的 JWT Token 生成、解析与验证。
 * 使用 HMAC-SHA256 签名算法，Token 中包含用户名（subject）、签发时间和过期时间。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@Component
public class JwtUtil {

    /** JWT 签名密钥（从配置文件读取，需至少 32 字符） */
    @Value("${jwt.secret:easycloud-default-secret-key-must-be-32-chars-long!!}")
    private String secret;

    /** JWT Token 有效期（毫秒），默认 86400000ms = 24小时 */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 获取 HMAC-SHA256 签名密钥
     *
     * @return SecretKey 实例
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     * <p>
     * Token 包含：subject(用户名)、iat(签发时间)、exp(过期时间)
     *
     * @param username 用户名（作为 Token 的 subject）
     * @return 签名后的 JWT Token 字符串
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中解析用户名
     *
     * @param token JWT Token 字符串
     * @return 用户名（Token 的 subject 字段）
     * @throws io.jsonwebtoken.JwtException Token 无效或已过期时抛出异常
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证 Token 是否有效（未过期且签名正确）
     *
     * @param token JWT Token 字符串
     * @return true=有效，false=无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析 JWT Token，返回 Claims 载荷
     *
     * @param token JWT Token 字符串
     * @return Claims 对象（包含 subject、expiration 等信息）
     * @throws io.jsonwebtoken.JwtException Token 格式错误或签名不匹配
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
