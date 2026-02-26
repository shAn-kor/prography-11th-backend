# prography-11th-backend

프로그라피 출결관리 Backend 과제 프로젝트입니다.

## 개발 환경
- JDK: 21
- Framework: Spring Boot
- DB: H2

## 실행 방법
1. 프로젝트 루트에서 실행:
```bash
./gradlew bootRun
```
2. 서버 실행 후 기본 URL:
- `http://localhost:8080`

### 테스트 실행
```bash
./gradlew test
```

## 요구사항 체크
- 필수 API 16개 구현
- 가산점 API 9개 구현
- H2 기반 동작
- 시드 데이터 로딩 (10기/11기, 파트, 팀, admin, 초기 보증금)

## 문서 위치
- 과제 원문/요구사항: [`docs/PROJECT.md`](./docs/PROJECT.md)
- 요구사항 상세: [`docs/design/01-requirements.md`](./docs/design/01-requirements.md)
- 시퀀스 다이어그램: [`docs/design/02-sequence-diagrams.md`](./docs/design/02-sequence-diagrams.md)
- 클래스 다이어그램: [`docs/design/03-class-diagram.md`](./docs/design/03-class-diagram.md)
- ERD: [`docs/design/04-erd.md`](./docs/design/04-erd.md)
- ADR 인덱스: [`docs/design/adr/README.md`](./docs/design/adr/README.md)

## 내 생각/설계 흔적 문서 경로
- ADR 전체: [`docs/design/adr/`](./docs/design/adr/)
- 구현 체크리스트: [`IMPLEMENTATION_CHECKLIST.md`](./IMPLEMENTATION_CHECKLIST.md)
- 작업 가이드: [`AGENT_GUIDE.md`](./AGENT_GUIDE.md)

## 참고 사항
- GitHub 제출 조건:
  - Public Repository
  - Repository name: `prography-11th-backend`
  - 2026-02-26 24:00 이후 커밋은 채점 제외
