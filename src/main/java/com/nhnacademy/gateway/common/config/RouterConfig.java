package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
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
                .route(
                        "AUTH-SERVICE",
                        r -> r
                                .path(
                                        "/api/auth/**"
                                )
                                .filters(this::strip)
                                .uri("lb://AUTH-SERVICE")
                )
                .route("SENSOR-SERVICE",
                        r -> r
                                .path(
                                        "/api/sensor-data-mappings/**"
                                )
                                .filters(this::strip)
                                .uri("lb://SENSOR-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/api/roles/**",
                                        "/api/event-levels/**",
                                        "/api/departments",
                                        "/api/departments/**",
                                        "/api/images/**",
                                        "/api/main/**"
                                )
                                .filters(this::strip)
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "AI-ANALYSIS-RESULT-SERVICE",
                        r -> r
                                .path(
                                        "/api/analysis-results/**",
                                        "/api/admin/analysis-results/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://AI-ANALYSIS-RESULT-SERVICE")
                )
                .route(
                        "USER-SERVICE",
                        r -> r
                                .path(
                                        "/api/admin/users/**",
                                        "/api/users/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://USER-SERVICE")
                )
                .route(
                        "EVENT-SERVICE",
                        r -> r
                                .path(
                                        "/api/events/**",
                                        "/api/notifications/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://EVENT-SERVICE")
                )
                .route(
                        "SERVER-RESOURCE-SERVICE",
                        r -> r
                                .path("/api/profile-image/**")
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://SERVER-RESOURCE-SERVICE")
                )
                .route(
                        "GATEWAY-SERVICE",
                        r -> r
                                .path(
                                        "/api/gateways/**",
                                        "/department-id/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://GATEWAY-SERVICE")
                )
                .route(
                        "SENSOR-SERVICE",
                        r -> r
                                .path(
                                        "/api/sensors/**",
                                        "/api/threshold-histories/**",
                                        "/api/sensor-data-mappings/**",
                                        "/api/data-types/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://SENSOR-SERVICE")
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
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://DASHBOARD-SERVICE")
                )
                .route(
                        "CORRELATION-ANALYSIS-SERVICE",
                        r -> r
                                .path(
                                        "/api/correlation-analyze/**"
                                )
                                .filters(this::stripAndJwtAuthorizationFilter)
                                .uri("lb://CORRELATION-ANALYSIS-SERVICE")
                )
                .build();
    }

    private GatewayFilterSpec strip(GatewayFilterSpec f) {
        return f.stripPrefix(1);
    }

    private GatewayFilterSpec stripAndJwtAuthorizationFilter(GatewayFilterSpec f) {
        return strip(f).filter(jwtAuthorizationFilter);
    }
}
