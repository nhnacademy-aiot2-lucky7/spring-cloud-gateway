package com.nhnacademy.gateway.common.config;

import com.nhnacademy.gateway.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.RouteMetadataUtils;
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
                        "your-spring-application-name",
                        r -> r
                                .header("X-USER-ID", "your-web-spring-application")
                                /*.and().cookie("access_token")*/
                                .and().path("/admin/**")
                                .filters(
                                        f ->
                                                f.filter(
                                                        jwtAuthorizationFilter,
                                                        jwtAuthorizationFilter.getOrder()
                                                )/*.filter(
                                                        // 추가로 검증할 Filter를 추가해주세요.
                                                )*/
                                )
                                .metadata(RouteMetadataUtils.CONNECT_TIMEOUT_ATTR, 1000)
                                .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, 3000)
                                .uri("lb://your-spring-application-name")
                )
                .build();
    }
}
