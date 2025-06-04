package com.nhnacademy.gateway.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Spring Cloud Eureka에서 사용할 캐시 구현체를 설정하는 구성 클래스입니다.
 */
@Configuration
public class CacheConfig {

    /**
     * Caffeine 기반 캐시 매니저를 생성하여 반환합니다.
     * 캐시 만료 시간과 최대 저장 항목 개수를 설정합니다.
     */
    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES) // 캐시 만료 시간 설정
                        .maximumSize(50) // 캐시에 저장할 최대 항목(키-값 쌍) 개수 설정
        );
        return cacheManager;
    }
}
