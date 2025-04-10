package com.nhnacademy.gateway.common.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RouterConfigTest {

    @Autowired
    private RouteLocator routeLocator;

    @DisplayName("Routes: 조회")
    @Test
    void testRoutes() {
        AtomicInteger index = new AtomicInteger();

        log.debug("============================================================");
        routeLocator
                .getRoutes()
                .subscribe(route ->
                        log.debug("Registered Route: {}", route.getId())
                );
        log.debug("");
    }

    /*@DisplayName("Routes: ")
    @Test
    void testCustomRouteLocator() {
        StepVerifier
                .create(routeLocator.getRoutes().collectList())
                .expectNextMatches(routes ->
                        routes
                                .stream()
                                .anyMatch(route -> {
                                    log.debug("Route ID: {}", route.getId());
                                    return route.getId().equals("spring-web-api");
                                })
                )
                .verifyComplete();
    }*/
}
