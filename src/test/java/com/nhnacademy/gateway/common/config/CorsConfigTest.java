//package com.nhnacademy.gateway.common.config;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.server.RequestPath;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import org.springframework.web.server.ServerWebExchange;
//
//import java.net.URI;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//public class CorsConfigTest {
//
//    CorsConfig corsConfig = new CorsConfig();
//
//    @Test
//    @DisplayName("CorsWebFilter 빈이 정상 생성되는지 확인")
//    void corsFilterBean_isCreated() {
//        assertThat(corsConfig.corsFilter()).isNotNull();
//    }
//
//    @Test
//    @DisplayName("CORS 설정에서 허용된 출처, 헤더, 메서드, 자격증명 설정이 올바른지 검증")
//    void corsConfigurationSource_containsExpectedSettings() {
//        UrlBasedCorsConfigurationSource source = corsConfig.corsConfigurationSource();
//
//        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
//        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
//        RequestPath requestPath = Mockito.mock(RequestPath.class);
//
//        when(exchange.getRequest()).thenReturn(request);
//        when(request.getURI()).thenReturn(URI.create("https://example.com/test-path"));
//        when(request.getPath()).thenReturn(requestPath);
//        when(requestPath.pathWithinApplication()).thenReturn(requestPath);
//        when(requestPath.value()).thenReturn("/test-path");
//
//        CorsConfiguration config = source.getCorsConfiguration(exchange);
//
//        assertThat(config).isNotNull();
//        assertThat(config.getAllowedOrigins()).containsExactly("https://luckyseven.live");
//        assertThat(config.getAllowedHeaders()).containsExactlyInAnyOrder("Content-Type", "X-USER-ID");
//        assertThat(config.getAllowCredentials()).isTrue();
//        assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder(
//                HttpMethod.GET.name(),
//                HttpMethod.POST.name(),
//                HttpMethod.PUT.name(),
//                HttpMethod.DELETE.name(),
//                HttpMethod.OPTIONS.name()
//        );
//    }
//}
