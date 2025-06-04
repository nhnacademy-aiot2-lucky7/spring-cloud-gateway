package com.nhnacademy.gateway.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

/**
 * CORS 정책을 설정하는 Config 영역입니다.
 */
@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Bean
    public CorsWebFilter corsFilter() {
        return new CorsWebFilter(corsConfigurationSource());
    }

    /**
     * 특정 출처(origin)에서 오는 해당하는 HTTP Method 요청만 허용하도록 설정된 CORS 정책을 반환합니다.
     */
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // cross-origin 요청이 허용되는 출처를 설정하십시오.
        corsConfig.addAllowedOrigin("https://luckyseven.live");
        corsConfig.addAllowedOrigin("https://www.luckyseven.live");
        corsConfig.addAllowedOrigin("https://localhost:10231");

        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedHeaders(List.of("Content-Type", "X-USER-ID", "Authorization"));

        // 요청으로 허용하는 HTTP Methods를 설정하십시오.
        corsConfig.setAllowedMethods(
                List.of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                )
        );

        // 커스텀한 설정 값을 path에 매핑
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}
