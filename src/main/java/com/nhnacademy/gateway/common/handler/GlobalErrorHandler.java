package com.nhnacademy.gateway.common.handler;

import com.nhnacademy.gateway.common.exception.CommonHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * <h2>GlobalErrorHandler</h2>
 * <p>
 * WebFlux 기반의 애플리케이션에서 발생하는 전역 예외를 처리하는 핸들러 클래스입니다.
 * </p>
 *
 * <h3>주요 기능</h3>
 * <ul>
 *     <li>공통 예외(CommonHttpException) 처리</li>
 *     <li>기타 처리되지 않은 예외 로깅 및 500 응답 반환</li>
 *     <li>JSON 형태의 오류 메시지를 클라이언트에 반환</li>
 * </ul>
 *
 * <p>
 * Spring Boot에서 {@link ErrorWebExceptionHandler}를 구현하여 글로벌하게 예외를 처리합니다.
 * </p>
 *
 * @author HwangSlater
 */
@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    private static final int DEFAULT_STATUS_CODE = 500;
    private static final String DEFAULT_MESSAGE = "게이트웨이 내부 오류 발생";

    /**
     * 전역 예외 처리 메서드
     *
     * @param exchange 예외가 발생한 요청과 응답을 포함하는 {@link ServerWebExchange}
     * @param ex       처리할 예외 객체
     * @return 비동기 Mono 객체
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        int statusCode = DEFAULT_STATUS_CODE;
        String message = DEFAULT_MESSAGE;

        // 커스텀 예외가 발생한 경우, 예외에 포함된 상태 코드와 메시지 사용
        if (ex instanceof CommonHttpException commonEx) {
            statusCode = commonEx.getStatusCode();
            message = commonEx.getMessage();
        }

        // 상태 코드 설정
        response.setStatusCode(HttpStatus.resolve(statusCode));

        // JSON 응답 생성
        String body = String.format("{\"status\": %d, \"message\": \"%s\"}", statusCode, message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
