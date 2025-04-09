package com.nhnacademy.gateway.common.filter;

import com.nhnacademy.gateway.common.util.JwtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GatewayFilter {

    @Getter
    private final int order = 1;

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object object = exchange.getRequest().getCookies().get("access_token");
        String token = object.toString();
        

        /*for (HttpCookie httpCookie : httpCookies) {
            // TODO: 실제 cookie 내부에 어떠한 방식으로 데이터가 담겨서 올지 모르겠습니다.
            if (jwtUtil.isValidToken(httpCookie.getValue())) {

            }
        }*/
        return Mono.empty();
    }
}
