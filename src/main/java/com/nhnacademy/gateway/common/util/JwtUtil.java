package com.nhnacademy.gateway.common.util;

import com.nhnacademy.gateway.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Objects;

/**
 * JWT 토큰의 유효성 검증 및 클레임 정보 추출을 담당하는 유틸리티 클래스입니다.
 * 다음과 같은 기능을 제공합니다:
 * - 토큰의 유효성 검사
 * - 사용자 ID(subject) 추출
 * - 커스텀 클레임 추출
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * application.properties 또는 환경변수에서 주입받는 JWT 서명용 비밀 키 문자열
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT 서명 검증에 사용되는 Key 객체
     */
    private Key secretKey;

    /**
     * 빈 초기화 시 실행되며, 비밀 키 문자열을 기반으로 HMAC 서명 키를 생성합니다.
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 주어진 JWT 토큰의 서명과 구조의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token) {
        try {
            isValidString(token, "JWT 토큰");

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT 토큰이 만료되었습니다: {}", e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.warn("지원하지 않거나 변조된 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 비어있거나 유효하지 않습니다.");
        }

        return false;
    }

    /**
     * JWT 토큰에서 사용자 ID(subject)를 추출합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 사용자 ID(subject)
     * @throws UnauthorizedException 토큰이 유효하지 않거나 파싱에 실패한 경우
     */
    public String getUserId(String token) {
        try {
            isValidString(token, "JWT 토큰");

            return getClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            String message = e.getMessage();
            throw new UnauthorizedException(
                    (Objects.nonNull(message) && !message.isEmpty())
                            ? message : "JWT에서 사용자 ID 추출에 실패"
            );
        }
    }

    /**
     * JWT 토큰에서 특정 클레임 값을 문자열로 추출합니다.
     *
     * @param token     JWT 토큰 문자열
     * @param claimName 추출할 클레임 이름 (예: "role", "email", "nickname")
     * @return 해당 클레임 값
     * @throws UnauthorizedException 클레임 추출에 실패한 경우
     */
    public String getClaimValue(String token, String claimName) {
        try {
            isValidString(token, "JWT 토큰");
            isValidString(claimName, "클레임 이름");

            String claimValue = (String) getClaims(token).get(claimName);
            if (Objects.isNull(claimValue)) {
                throw new JwtException("claimValue is null");
            }

            return claimValue;
        } catch (JwtException | IllegalArgumentException e) {
            String message = e.getMessage();
            throw new UnauthorizedException(
                    (Objects.nonNull(message) && !message.isEmpty())
                            ? message : "JWT에서 클레임 값 추출에 실패"
            );
        }
    }

    /**
     * JWT 토큰에서 Claims(페이로드) 정보를 추출합니다.
     *
     * @param token JWT 토큰 문자열
     * @return Claims 객체
     * @throws JwtException             파싱 실패 또는 서명 검증 실패 시
     * @throws IllegalArgumentException 토큰이 null이거나 잘못된 경우
     */
    private Claims getClaims(String token) throws JwtException, IllegalArgumentException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 입력 문자열이 null이거나 비어있는 경우 예외를 발생시킵니다.
     *
     * @param value     검사할 문자열
     * @param fieldName 예외 메시지에 사용될 필드 이름
     * @throws IllegalArgumentException 유효하지 않은 문자열일 경우
     */
    private void isValidString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("%s이(가) null이거나 비어 있습니다.".formatted(fieldName));
        }
    }
}
