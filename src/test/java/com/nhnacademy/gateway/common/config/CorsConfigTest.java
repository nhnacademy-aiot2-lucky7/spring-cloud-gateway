package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.HeaderAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class CorsConfigTest {

    @Autowired
    private CorsWebFilter corsFilter;

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
    void test() {
        HeaderAssertions header =
                webTestClient.get()
                        .uri("/admin/test")
                        .header(HttpHeaders.ORIGIN, "https://luckyseven.live")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .cookie("access_token", validToken)
                        .exchange()
                        .expectHeader();

        // log.debug("Origin: {}", header.exists(HttpHeaders.ORIGIN));
        // log.debug("Access-Control-Allow-Origin: {}", header.exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));

                /*.exchange()
                .expectHeader().valueEquals(
                        "Access-Control-Allow-Origin",
                        "https://luckyseven.live"
                )
                .expectHeader().exists("Access-Control-Allow-Methods");*/
    }

    /*@Test
    void shouldAllowCors_whenOriginIsAllowed() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
                .options("/admin/method-get")
                .header(HttpHeaders.ORIGIN, "https://luckyseven.live")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // When
        corsFilter.filter(exchange, serverWebExchange -> Mono.empty()).block();

        // Then
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();

        assertEquals("https://luckyseven.live", responseHeaders.getAccessControlAllowOrigin());
        assertTrue(responseHeaders.getAccessControlAllowMethods().contains(HttpMethod.GET));
    }*/

}
