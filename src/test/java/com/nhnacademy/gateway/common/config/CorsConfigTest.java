package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class CorsConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        Key secretKey = (Key) secretKeyField.get(jwtUtil);

        Date now = new Date();
        validToken =
                Jwts.builder()
                        .setSubject("testUser")
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + (long) 60000))
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    @Test
    @DisplayName("CORS Preflight 요청에 대해 허용된 origin과 method가 허용되는지 검증")
    void shouldAllowCors_whenOriginIsAllowed() {
        webTestClient.options()
                .uri("/admin/test")
                .header(HttpHeaders.ORIGIN, "https://luckyseven.live")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .cookie("access_token", validToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                .expectHeader().valueEquals(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://luckyseven.live"
                )
                .expectHeader().exists(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)
                .expectHeader().valueEquals(
                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                        HttpMethod.GET.name()
                );
    }

    @Test
    @DisplayName("실제 GET 요청 시 CORS 헤더가 응답에 포함되는지 확인")
    void shouldIncludeCorsHeadersOnActualRequest_whenOriginIsAllowed() {
        webTestClient.get()
                .uri("/admin/test")
                .header(HttpHeaders.ORIGIN, "https://luckyseven.live")
                .cookie("access_token", validToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://luckyseven.live"
                );
    }
}
