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

    /**
     * JWT 인증 필터
     */
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("SENSOR-SERVICE",
                        r -> r
                                .path("/sensor-data-mappings/**")
                                .uri("lb://SENSOR-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/admin/**",
                                        "/departments/**",
                                        "/users/**",
                                        "/roles/**",
                                        "/event-levels/**"
                                )
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "AUTH-SERVICE",
                        r -> r
                                .path("/auth/**")
                                .uri("lb://AUTH-SERVICE")
                )
                .route(
                        "EVENT-SERVICE",
                        r -> r
                                .path("/events/**")
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://EVENT-SERVICE")
                )
                .route(
                        "SERVER-RESOURCE-SERVICE",
                        r -> r
                                .path("/profile-image/**")
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://SERVER-RESOURCE-SERVICE")
                )
                .build();
    }
}
