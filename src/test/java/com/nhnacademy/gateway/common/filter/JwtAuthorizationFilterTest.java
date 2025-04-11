package com.nhnacademy.gateway.common.filter;

import com.nhnacademy.gateway.common.exception.UnauthorizedException;
import com.nhnacademy.gateway.common.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JwtAuthorizationFilterIntegrationTest {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private JwtUtil jwtUtil;

    private Key secretKey;

    private String validToken;

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
    @DisplayName("JWT 토큰이 있을 경우 X-User-Id 헤더 추가")
    void shouldAddHeader_whenValidTokenProvided() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .cookie(new HttpCookie("access_token", validToken))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain dummyChain = e -> {
            String userIdHeader = e.getRequest().getHeaders().getFirst("X-User-Id");
            assertThat(userIdHeader).isEqualTo("testUser");
            return Mono.empty();
        };

        jwtAuthorizationFilter.filter(exchange, dummyChain).block();
    }

    @Test
    @DisplayName("JWT 토큰이 없을 경우 X-User-Id 헤더 없음")
    void shouldNotAddHeader_whenNoTokenProvided() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain dummyChain = e -> {
            String userIdHeader = e.getRequest().getHeaders().getFirst("X-User-Id");
            assertThat(userIdHeader).isNull();
            return Mono.empty();
        };

        jwtAuthorizationFilter.filter(exchange, dummyChain).block();
    }

    @Test
    @DisplayName("토큰이 변조된 경우 - UnauthorizedException 발생")
    void shouldThrowUnauthorizedException_whenTokenIsTampered() {
        String tamperedToken = validToken + "tampered";

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/admin/test")
                .cookie(new HttpCookie("access_token", tamperedToken))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        assertThrows(UnauthorizedException.class, () -> jwtAuthorizationFilter.filter(exchange, chain).block());
    }

    @Test
    @DisplayName("토큰이 만료된 경우 - UnauthorizedException 발생")
    void shouldThrowUnauthorizedException_whenTokenIsExpired() {
        String expiredToken = createTestToken("expiredUser", -1000); // 이미 만료된 토큰

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/admin/test")
                .cookie(new HttpCookie("access_token", expiredToken))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> jwtAuthorizationFilter.filter(exchange, chain).block());

        assertThat(exception.getMessage()).isEqualTo("JWT가 만료되었습니다.");
    }

    @Test
    @DisplayName("access_token 쿠키는 있지만 값이 비어 있는 경우 - UnauthorizedException 발생")
    void shouldThrowUnauthorizedException_whenTokenIsEmptyInCookie() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/admin/test")
                .cookie(new HttpCookie("access_token", " "))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> jwtAuthorizationFilter.filter(exchange, chain).block());

        assertThat(exception.getMessage()).isEqualTo("JWT 토큰이 null이거나 비어 있습니다.");
    }

    @Test
    @DisplayName("JWT에서 사용자 ID 추출 실패 - subject가 null인 경우")
    void shouldThrowUnauthorizedException_whenSubjectIsNull() {
        Date now = new Date();
        String tokenWithoutSubject = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 60000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/admin/test")
                .cookie(new HttpCookie("access_token", tokenWithoutSubject))
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> jwtAuthorizationFilter.filter(exchange, chain).block());

        assertThat(exception.getMessage()).isEqualTo("JWT에서 사용자 ID 추출 실패");
    }
}