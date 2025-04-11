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

        // 테스트용 Secret 키 설정
        secretKey = "mySuperSecretKeyForJwtMustBeLongEnoughToSign";
        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);
        jwtUtil.init();

        // 유효한 토큰 생성
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
    @DisplayName("유효한 토큰은 예외 없이 통과해야 한다")
    void validateToken_valid() {
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("만료된 토큰은 UnauthorizedException을 발생시켜야 한다")
    void validateToken_expiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject(testUserId)
                .claim("role", testRole)
                .setIssuedAt(new Date(System.currentTimeMillis() - 60000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 이미 만료
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateToken(expiredToken));
    }

    @Test
    @DisplayName("null 토큰은 UnauthorizedException을 발생시켜야 한다")
    void validateToken_nullToken() {
        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("정상 토큰에서 사용자 ID 추출 성공")
    void getUserId_validToken() {
        String userId = jwtUtil.getUserId(token);
        assertEquals(testUserId, userId);
    }

    @Test
    @DisplayName("변조된 토큰은 UnauthorizedException을 발생시켜야 한다")
    void getUserId_tamperedToken() {
        String invalidToken = token + "tampered";
        assertThrows(UnauthorizedException.class, () -> jwtUtil.getUserId(invalidToken));
    }

    @Test
    @DisplayName("null 토큰은 사용자 ID 추출 시 UnauthorizedException 발생")
    void getUserId_nullToken() {
        assertThrows(UnauthorizedException.class, () -> jwtUtil.getUserId(null));
    }
}
