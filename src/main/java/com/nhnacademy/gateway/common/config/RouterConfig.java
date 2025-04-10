package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        "spring-web-api",
                        r -> r
                                .path("/admin/**")
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://SPRING-WEB-API")
                                // 실패했을 경우, 로그인 페이지로 유도하는 리디렉션 이벤트를 추가 예정

                )
                .build();
    }
}
