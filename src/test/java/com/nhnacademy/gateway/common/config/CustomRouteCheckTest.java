package com.nhnacademy.gateway.common.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest
class CustomRouteCheckTest {

    @Autowired
    private RouteLocator routeLocator;

    @DisplayName("Routes: 조회")
    @Test
    void testRoutes() {
        AtomicInteger index = new AtomicInteger();
        log.debug("==================================================================================================");
        routeLocator
                .getRoutes()
                .subscribe(route ->
                        log.debug("No.{} - Route: {}", index.incrementAndGet(), route.getId())
                );
        log.debug("==================================================================================================");
    }
}
