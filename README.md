# 📰 NewsTracker – 키워드 기반 뉴스 수집 시스템

Google OAuth 로그인, JWT 인증, 주기적 뉴스 수집을 지원하는 Spring Boot 기반 API 프로젝트입니다.

---

## ✅ 주요 기능

- Google OAuth2 로그인 → JWT Access/Refresh 토큰 발급
- 관심 키워드 등록 및 삭제
- 사용자 키워드 기반 뉴스 주기적 수집 (`@Scheduled`)
- 중복 뉴스 저장 방지
- Swagger를 통한 API 문서 제공

---

## 🧱 기술 스택

| 항목       | 기술 |
|------------|------|
| Language   | Java 17 |
| Framework  | Spring Boot 3.x |
| Build Tool | Gradle |
| DB         | H2 (테스트), MariaDB (운영 확장 가능) |
| Auth       | Spring Security + OAuth2 + JWT |
| 외부 API   | Naver 뉴스 검색 API |
| 문서화     | SpringDoc OpenAPI (Swagger UI) |
| 테스트     | JUnit5 + `@MockitoBean` 기반 단위/통합 테스트 |

---

## 📁 프로젝트 구조

```
com.example.newstracker
├── controller/              # API 진입점
├── domain/                  # user, keyword, news 도메인 패키지
├── common/                  # jwt, exception, response
├── config/                  # Security, OAuth, RestClient 설정
├── scheduler/               # NewsScheduler (주기적 수집)
└── dto/                     # DTO (record + @Schema)
```

---

## 🔐 인증 구조 (JWT + Google OAuth)

- 로그인 경로: `GET /oauth2/authorization/google` (브라우저 리디렉션)
- 로그인 성공 후 JWT 발급
- `Authorization: Bearer {accessToken}` 방식으로 API 호출

---

## 🧪 API 문서 (Swagger)

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- AccessToken 발급 후 상단 "Authorize" 버튼으로 토큰 입력 가능

---

## 🧠 테스트 전략

- 모든 테스트에 `@MockitoBean` 사용 (`@MockBean` 금지)
- NewsScheduler 테스트: 일부 실패 → 전체 흐름 유지 여부 검증
- NewsService 테스트: 중복 저장 방지, 예외 발생 시 동작 검증

---

## ⏰ 뉴스 수집 스케줄링 배치 상세

### ✅ 목적
- 사용자가 등록한 키워드에 대해, 외부 뉴스 API(Naver)를 호출하여 관련 뉴스를 주기적으로 수집합니다.
- 이미 저장된 뉴스는 중복 저장되지 않으며, 저장 실패 또는 API 실패 시에도 전체 흐름은 중단되지 않습니다.

### ✅ 스케줄러 구성

- 클래스: `NewsScheduler`
- 주기: `@Scheduled(fixedDelay = 600000)` → 10분마다 한 번 실행
- 대상: `KeywordRepository.findAll()`로 조회된 모든 키워드

### ✅ 처리 흐름

1. 모든 키워드 조회
2. 각 키워드별로 Naver 뉴스 API 호출
3. 응답 받은 뉴스 리스트 순회
4. 뉴스 저장 시 중복 링크 필터링 (`existsByUserAndLink`)
5. 예외 발생 시 `log.warn()`으로 기록, 다음 키워드로 계속 진행

### ✅ 예외 처리 전략

- API 호출 실패 → `List.of()` 반환 + 로그 출력
- 뉴스 저장 중 오류 → `try-catch`로 보호 후 로그만 출력

### ✅ 테스트 커버리지

- 일부 키워드가 실패하더라도 전체 흐름은 중단되지 않는지 테스트
- 중복 뉴스 저장이 차단되는지 테스트
- 날짜 파싱 예외 등 비정상 응답에 대한 방어 로직 포함

### ✅ 향후 확장 가능

- Spring Retry 기반 재시도
- 실패 키워드에 대한 Slack 알림
- 키워드별 통계 수집 (성공/실패 건수)

- 매 10분마다 키워드 목록 조회
- 각 키워드별 Naver API 호출
- 중복 뉴스 필터링 후 저장 (`existsByUserAndLink()` 활용)

---

## 🚀 실행 방법

1. `.env` 파일 생성:

```env
BASE_URL=http://localhost:8080
SECRET_KEY=...
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
NAVER_CLIENT_ID=...
NAVER_CLIENT_SECRET=...
```

2. 프로젝트 실행: `./gradlew bootRun`

3. 브라우저에서 `http://localhost:8080/swagger-ui.html` 접속

---

## 🔮 향후 확장 포인트

- 키워드별 뉴스 알림 (Email, Slack)
- Redis + Bloom Filter 중복 필터링
- 뉴스 요약 API 연동 (GPT 등)
- React 프론트 연동

---

## 📄 라이센스

MIT License © 2025 jisu bak