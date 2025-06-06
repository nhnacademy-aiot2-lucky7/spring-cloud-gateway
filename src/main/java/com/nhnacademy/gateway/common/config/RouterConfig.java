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
                                .path("/api/sensor-data-mappings/**")
                                .filters(f->f.stripPrefix(1))
                                .uri("lb://SENSOR-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/api/roles/**",
                                        "/api/event-levels/**",
                                        "/api/departments",
                                        "/api/departments/**"
                                )
                                .filters(f->f.stripPrefix(1))
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/api/admin/users/**",
                                        "/api/users/**"
                                )
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "AUTH-SERVICE",
                        r -> r
                                .path("/api/auth/**")
                                .filters(f->f.stripPrefix(1))
                                .uri("lb://AUTH-SERVICE")
                )
                .route(
                        "EVENT-SERVICE",
                        r -> r
                                .path(
                                        "/api/events/**",
                                        "/api/notifications/**"
                                )
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://EVENT-SERVICE")
                )
                .route(
                        "SERVER-RESOURCE-SERVICE",
                        r -> r
                                .path("/api/profile-image/**")
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://SERVER-RESOURCE-SERVICE")
                )
                .route(
                        "GATEWAY-SERVICE",
                        r -> r
                                .path("/api/gateways/**")
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://GATEWAY-SERVICE")
                )
                .route(
                        "SENSOR-SERVICE",
                        r -> r
                                .path(
                                        "/api/sensors/**",
                                        "/api/threshold-histories",
                                        "/api/sensor-data-mappings",
                                        "/api/data-types"
                                )
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://SENSOR-SERVICE")
                )
                .route(
                        "AI-ANALYSIS-RESULT-SERVICE",
                        r -> r
                                .path(
                                        "/api/analysis-results/**",
                                        "/api/admin/analysis-results/**"
                                )
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://AI-ANALYSIS-RESULT-SERVICE")
                )
                .route(
                        "DASHBOARD-SERVICE",
                        r -> r
                                .path(
                                        "/api/dashboards/**",
                                        "/api/panels/**",
                                        "/api/folders/**",
                                        "/api/test/**"
                                )
                                .filters(f -> f.stripPrefix(1).filter(jwtAuthorizationFilter))
                                .uri("lb://DASHBOARD-SERVICE")
                )
                .build();
    }
}