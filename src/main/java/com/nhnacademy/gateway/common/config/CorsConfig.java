package com.nhnacademy.gateway.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

/**
 * CORS 정책을 설정하는 Config 클래스입니다. <br>
 * 해당 설정은 Spring Cloud Gateway의 전역 CORS 필터로 작동합니다.
 */
@Slf4j
@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    /**
     * 전역 CORS 필터 Bean 등록 <br>
     * - Gateway 수준에서 CORS를 제어하며, 각 라우터 또는 서비스 단의 설정과는 별도로 적용됩니다.
     */
    @Bean
    CorsWebFilter corsFilter() {
        return new CorsWebFilter(corsConfigurationSource());
    }

    /**
     * CORS 설정을 정의하고, 이를 특정 URL 경로에 매핑합니다.
     *
     * @return UrlBasedCorsConfigurationSource 정의된 CORS 정책을 URL 패턴에 적용하는 소스
     */
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Cross-Origin-Resource Sharing 요청을 허용할 출처(origin)를 설정하는 패턴입니다
        corsConfig.setAllowedOriginPatterns(allowedOrigins);

        // 클라이언트 요청 시 허용할 HTTP Header 목록
        corsConfig.setAllowedHeaders(
                List.of(
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.AUTHORIZATION,
                        "X-USER-ID"  // 사용자 정의 헤더 예시
                )
        );

        // 쿠키, 인증 정보 등을 포함한 요청 허용
        corsConfig.setAllowCredentials(true);

        // 허용할 HTTP Method 목록 설정
        corsConfig.setAllowedMethods(
                List.of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name() // Preflight 요청을 위함
                )
        );

        // 정의된 CORS 설정을 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}
