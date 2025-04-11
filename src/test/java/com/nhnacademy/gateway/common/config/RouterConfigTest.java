package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;

// 통합 테스트
@Slf4j
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RouterConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}")
    private String secretKey;

    private String testToken;

    @BeforeEach
    void setUp() {
        testToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Disabled("'/admin/**'에 해당하는 Spring API Service를 아직 등록하지 않은 관계로, 성공했음을 확인할 수 없습니다.")
    @DisplayName("'/admin/**' 요청 성공: access_token 존재")
    @Test
    void testRoutePathAdmin() {
        webTestClient.get()
                .uri("/admin/test")
                .cookie("access_token", testToken)
                .exchange()
                .expectHeader().exists("X-User-Id")
                .expectHeader().valueEquals("X-User-Id", "testUser");
    }

    @DisplayName("'/admin/**' 요청 실패: access_token 부재")
    @Test
    void testRoutePathAdmin_WhenNotAccessTokenIsMissing() {
        webTestClient.get()
                .uri("/admin/test")
                .exchange()
                .expectStatus().is5xxServerError(); // RuntimeException 발생 기준
    }
}
