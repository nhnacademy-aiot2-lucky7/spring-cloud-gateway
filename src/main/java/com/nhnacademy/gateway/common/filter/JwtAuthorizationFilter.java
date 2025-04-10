package com.nhnacademy.gateway.common.filter;

import com.nhnacademy.gateway.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <h2>Info</h2>
 * JWT 토큰 내에 존재하는 클레임 정보를 추출하여 전달하는 게이트웨이 필터 클래스입니다.
 * <hr>
 * - <b>1. 토큰이 존재하는 경우</b> <br>
 * 토큰 내에 존재하는 {@code userId}와 {@code userRole} 클레임 정보를 추출하여 전달합니다.
 * <hr>
 * - <b>2. 토큰이 존재하지 않는 경우</b> <br>
 * {@code userId}와 {@code userRole}에, 기본 값({@code guest})을 할당합니다.
 *
 * @author Rayhke
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get("access_token");
        String userId = "guest";
        String userRole = "guest";

        if (Objects.nonNull(cookies) && !cookies.isEmpty()) {
            String token = cookies.getFirst().getValue();
            if (jwtUtil.isValidToken(token)) {
                userId = jwtUtil.getClaimValue(token, "userId");
                userRole = jwtUtil.getClaimValue(token, "userRole");
            }
        }

        if (!userRole.equals("admin")) {
            throw new RuntimeException();
        }

        Map<String, Object> attribute = exchange.getAttributes();
        attribute.put("userId", userId);
        attribute.put("userRole", userRole);

        return chain.filter(exchange);
    }
}
