package com.wkr.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    // 密钥（至少 32 个字符）
    private static final String SECRET = "smart-ai-secret-key-2026-1234567890";

    // 过期时间：7天（毫秒）
    private static final Long EXPIRE = 1000 * 60 * 60 * 24 * 7L;

    /**
     * 获取密钥对象
     */
    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT
     */
    public static String createToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)                    // 用 subject 替代 setSubject
                .claim("userId", userId)
                .issuedAt(new Date())                 // 用 issuedAt 替代 setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + EXPIRE)) // 用 expiration 替代 setExpiration
                .signWith(getKey())                   // 传入 SecretKey 对象
                .compact();
    }

    /**
     * 解析 JWT
     */
    public static Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 JWT 是否有效
     */
    public static boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}