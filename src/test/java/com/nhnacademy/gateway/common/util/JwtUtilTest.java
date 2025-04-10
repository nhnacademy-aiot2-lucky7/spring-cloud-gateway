package com.nhnacademy.gateway.common.util;

import com.nhnacademy.gateway.common.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;
    private String secretKey;
    private final String testUserId = "testUser";
    private final String testRole = "ROLE_USER";
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // 테스트용 Secret 키
        secretKey = "mySuperSecretKeyForJwtMustBeLongEnoughToSign";
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);
        jwtUtil.init();

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        token = Jwts.builder()
                .setSubject(testUserId)
                .claim("role", testRole)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000)) // 1분
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    @DisplayName("토큰 유효성 검사")
    void isValidToken() {
        assertTrue(jwtUtil.isValidToken(token));
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 만료된 토큰일 경우")
    void isValidToken_exception1() {
        String expiredToken = Jwts.builder()
                .setSubject(testUserId)
                .claim("role", testRole)
                .setIssuedAt(new Date(System.currentTimeMillis() - 60000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 이미 만료됨
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.isValidToken(expiredToken));
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 토큰이 null일 경우")
    void isValidToken_exception2() {
        assertFalse(jwtUtil.isValidToken(null));
    }

    @Test
    @DisplayName("사용자 ID 추출")
    void getUserId() {
        String userId = jwtUtil.getUserId(token);
        assertEquals(testUserId, userId);
    }

    @Test
    @DisplayName("사용자 ID 추출 - 변조된 토큰일 경우")
    void getUserId_exception1() {
        String invalidToken = token + "tampered";
        assertThrows(UnauthorizedException.class, () -> jwtUtil.getUserId(invalidToken));
    }

    @Test
    @DisplayName("사용자 ID 추출 - null 토큰일 경우")
    void getUserId_exception2() {
        assertThrows(UnauthorizedException.class, () -> jwtUtil.getUserId(null));
    }
}
