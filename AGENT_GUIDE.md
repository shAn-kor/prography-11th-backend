# Codex Reference

이 문서는 Codex가 이 프로젝트에서 일관된 방식으로 작업하기 위한 빠른 참조 문서다.

## 1. 아키텍처 경계
- Controller: 요청/응답 변환만 담당
- Facade: 여러 Application Service 조합이 필요한 유스케이스만 조율
- Application Service: 유스케이스 실행, 트랜잭션 경계 담당
- Domain Model(Entity/VO/Policy): 순수 비즈니스 규칙 담당
- Repository: Infrastructure 계층, Application Service에서만 참조

## 2. 레이어 호출 규칙
- Controller -> Facade 또는 Application Service
- Facade -> Application Service only
- Application Service 간 직접 호출 금지 (크로스 도메인은 Facade 경유)
- Facade의 Repository 직접 접근 금지

## 3. 트랜잭션 규칙
- `@Transactional`은 Application Service에만 위치
- Facade/Domain Model에 `@Transactional` 금지
- 크로스 도메인 쓰기:
  - 1순위: Application Service 단일 트랜잭션 경계
  - 2순위: Facade 보상 로직

## 4. 시퀀스 다이어그램 규칙
- 파일: `docs/design/02-sequence-diagrams.md`
- 1 Feature = 1 Diagram
- API 호출은 HTTP 메서드 + 경로 표기
- 내부 호출은 한글 설명 중심 (구현 디테일 과다 금지)
- `@Transactional`, SQL 상세, Repository participant 표기 금지
- 표현 레벨은 Facade ↔ Application Service 중심

## 5. 현재 설계 문서
- 요구사항: `docs/design/01-requirements.md`
- 시퀀스: `docs/design/02-sequence-diagrams.md`
- 클래스: `docs/design/03-class-diagram.md`
- ERD: `docs/design/04-erd.md`
- ADR 인덱스: `docs/design/adr/README.md`

## 6. ADR 작성 규칙(프로젝트 적용)
- 위치: `docs/design/adr/`
- 형식: Status / Date / Context / Decision / Alternatives / Consequences
- 상태 기본값: `Proposed`
