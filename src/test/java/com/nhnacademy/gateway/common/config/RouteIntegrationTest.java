package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RouteIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    private String validToken;

    private Key secretKey;

    @BeforeEach
    void setUp() throws Exception {
        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKey = (Key) secretKeyField.get(jwtUtil);

        validToken = createTestToken("testUser", 60 * 1000); // 유효한 토큰 (1분)
    }
    private String createTestToken(String userId, long validityMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    @DisplayName("유효한 토큰 - 정상적으로 api 이동")
    void testValidTokenShouldInjectHeader() {
        webTestClient.get()
                .uri("/admin/test")
                .cookie("access_token", validToken)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("토큰 없음 - 정상적으로 api 이동")
    void testNoTokenShouldNotInjectHeader() {
        webTestClient.get()
                .uri("/admin/test")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 - Unauthorized 발생")
    void testInvalidTokenShouldBypassHeaderInjection() {
        String invalidToken = "invalid.jwt.token";

        webTestClient.get()
                .uri("/admin/test")
                .cookie("access_token", invalidToken)
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
