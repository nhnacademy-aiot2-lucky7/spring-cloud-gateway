# spring-cloud-gateway

## JWT 인증 흐름

게이트웨이는 `JwtUtil`을 활용하여 클라이언트 쿠키에 포함된 JWT 토큰을 추출하고, 해당 토큰의 유효성을 검증합니다.  
`JwtUtil`은 다음 기능들을 제공합니다:

- JWT 토큰 생성  
- 서명(Signature) 검증  
- 클레임(Claims) 추출  

이 유틸은 `JwtAuthorizationFilter`에서 사용되며, 필터는 토큰의 유효성을 확인한 후 클레임에서 `userId`를 추출합니다.  
추출된 `userId`는 이미 AES 방식으로 암호화되어있으며, 커스텀 헤더인 `X-User-Id`에 담겨 하위 API 서버로 전달됩니다.

---

## 에러 처리 정책

게이트웨이는 JWT 유효성 검증 실패 또는 내부 오류가 발생할 경우, 다음과 같은 응답을 클라이언트에게 반환합니다:

### 공통 응답 헤더
```
Content-Type: application/json
Status: 401 Unauthorized 또는 500 Internal Server Error
```

### 응답 Body 예시

- **500 Internal Server Error**
```json
{
  "status": 500,
  "message": "게이트웨이 내부 오류 발생"
}
```

- **401 Unauthorized**
```json
{
  "status": 401,
  "message": "토큰이(가) null이거나 비어 있습니다."
}
```

```json
{
  "status": 401,
  "message": "JWT에서 사용자 ID 추출에 실패"
}
```