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
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
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
                                        "/roles/**",
                                        "/event-levels/**",
                                        "/departments",
                                        "/departments/**"
                                )
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/admin/**",
                                        "/users/**"
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
                                .path(
                                        "/events/**",
                                        "/notifications/**"
                                )
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
                .route(
                        "GATEWAY-SERVICE",
                        r -> r
                                .path("/gateways/**")
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://GATEWAY-SERVICE")
                )
                .route(
                        "SENSOR-SERVICE",
                        r -> r
                                .path(
                                        "/sensors/**",
                                        "/threshold-histories",
                                        "/sensor-data-mappings",
                                        "/data-types"
                                )
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://SENSOR-SERVICE")
                )
                .route(
                        "AI-ANALYSIS-RESULT-SERVICE",
                        r -> r
                                .path(
                                        "/analysis-results/**",
                                        "/admin/analysis-results/**"
                                )
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://AI-ANALYSIS-RESULT-SERVICE")
                )
                .route(
                        "DASHBOARD-SERVICE",
                        r -> r
                                .path(
                                        "/dashboards/**",
                                        "/panels/**",
                                        "/folders/**"
                                )
                                .filters(f -> f.filter(jwtAuthorizationFilter))
                                .uri("lb://DASHBOARD-SERVICE")
                )
                .build();
    }
}