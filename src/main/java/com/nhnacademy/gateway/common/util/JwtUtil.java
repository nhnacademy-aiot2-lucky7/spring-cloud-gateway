package com.nhnacademy.gateway.common.util;

import com.nhnacademy.gateway.common.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Objects;

/**
 * JWT 유틸리티 클래스.
 *
 * <p>토큰 생성, 서명 검증, 클레임 추출 등 JWT 관련 기능을 제공합니다.</p>
 *
 * <h3>주요 기능</h3>
 * <ul>
 *     <li>토큰의 서명 및 만료 검증</li>
 *     <li>사용자 ID(subject) 및 커스텀 클레임 추출</li>
 * </ul>
 *
 * <p>
 * 유효하지 않은 토큰의 경우 {@link UnauthorizedException} 예외를 발생시킵니다.
 * </p>
 *
 * @author HwangSlater
 */
@Slf4j
@Component
public class JwtUtil {

    /** application.properties에서 주입받은 JWT 서명용 시크릿 키 */
    @Value("${jwt.secret}")
    private String secret;

    /** JWT 서명 및 검증에 사용하는 HMAC 키 객체 */
    private Key secretKey;

    /**
     * 빈 초기화 시 시크릿 문자열을 기반으로 서명 키를 생성합니다.
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * <p>만료되었거나 구조가 잘못된 토큰에 대해 {@link UnauthorizedException} 예외를 발생시킵니다.</p>
     *
     * @param token 검증할 JWT 문자열
     * @throws UnauthorizedException 유효하지 않은 토큰일 경우
     */
    public void validateToken(String token) {
        try {
            isValidJwtToken(token);

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("JWT가 만료되었습니다.");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new UnauthorizedException("지원하지 않거나 형식이 잘못된 JWT입니다.");
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("JWT 토큰이 비어 있거나 유효하지 않습니다.");
        }
    }

    /**
     * JWT에서 사용자 ID(subject)를 추출합니다.
     *
     * <p>유효하지 않은 토큰일 경우 {@link UnauthorizedException} 예외를 발생시킵니다.</p>
     *
     * @param token JWT 문자열
     * @return subject 값 (예: userId)
     * @throws UnauthorizedException 토큰이 유효하지 않을 경우
     */
    public String getUserId(String token) {
        try {
            isValidJwtToken(token);
            return getClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            String message = e.getMessage();
            throw new UnauthorizedException(
                    (Objects.nonNull(message) && !message.isEmpty())
                            ? message
                            : "JWT에서 사용자 ID 추출 실패"
            );
        }
    }

    /**
     * JWT에서 Claims(페이로드)를 추출합니다.
     *
     * @param token JWT 문자열
     * @return Claims 객체
     * @throws JwtException 서명 검증 실패 또는 잘못된 토큰 구조일 경우
     */
    private Claims getClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 전달된 문자열이 null이거나 비어 있는 경우 예외를 발생시킵니다.
     *
     * @param value 검사할 문자열
     * @throws IllegalArgumentException 값이 null이거나 공백 문자열인 경우
     */
    private void isValidJwtToken(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT 토큰이 null이거나 비어 있습니다.");
        }
    }
}
