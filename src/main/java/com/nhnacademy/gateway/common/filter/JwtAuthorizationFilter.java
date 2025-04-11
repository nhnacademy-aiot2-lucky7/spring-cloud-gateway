package com.nhnacademy.gateway.common.filter;

import com.nhnacademy.gateway.common.exception.ForbiddenException;
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
 * JWT 인증 필터.
 *
 * <p>Spring Cloud Gateway의 커스텀 필터로, 요청 쿠키에서 JWT를 추출해 유효성을 검증하고,
 * 유효한 경우 사용자 ID를 {@code X-User-Id} 헤더에 추가하여 다음 서비스로 전달합니다.</p>
 *
 * <h3>동작 방식</h3>
 * <ul>
 *     <li>access_token 쿠키가 존재하고, 토큰이 유효한 경우:
 *         <ul>
 *             <li>토큰에서 userId 클레임 추출</li>
 *             <li>추출한 userId를 {@code X-User-Id} 헤더에 추가</li>
 *         </ul>
 *     </li>
 *     <li>토큰이 없거나 유효하지 않은 경우:
 *         <ul>
 *             <li>요청을 수정하지 않고 그대로 다음 필터로 전달</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author Rayhke
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GatewayFilter {

    /** JWT 유틸리티 (토큰 검증 및 클레임 추출) */
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // access_token 쿠키 조회
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get("access_token");

        // 쿠키가 존재하고 비어있지 않다면
        if (Objects.nonNull(cookies) && !cookies.isEmpty()) {
            String token = cookies.getFirst().getValue();

            // 토큰이 변조 또는 잘못 되었을 경우 throw를 던지는 validate실행
            jwtUtil.validateToken(token);
            // userId 클레임 추출
            String userId = jwtUtil.getUserId(token);

            // 커스텀 헤더에 userId 담아서 요청 객체 수정
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder.header("X-User-Id", userId))
                    .build();

            // 수정된 요청 전달
            return chain.filter(mutatedExchange);
        }

        // 토큰이 없거나 유효하지 않으면 원본 요청 그대로 전달
        return chain.filter(exchange);
    }
}
