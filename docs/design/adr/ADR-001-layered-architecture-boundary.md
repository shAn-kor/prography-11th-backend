# ADR-001: 레이어드 아키텍처 경계 확정

- Status: Proposed
- Date: 2026-02-26

## Context
요구사항/설계 문서에서 레이어 역할과 호출 경계가 구현 기준이 되어야 한다. 문서 간 용어 차이로 해석 불일치가 발생했다.

## Decision
아키텍처 경계를 아래로 확정한다.
- Controller는 요청/응답 변환만 담당한다.
- Controller는 Facade 또는 Application Service를 호출한다.
- Application Service는 유스케이스 실행과 트랜잭션 경계를 담당한다.
- Domain Model(Entity/VO/Policy)은 순수 비즈니스 규칙만 담당한다.
- Repository는 Infrastructure에 위치하며 Application Service에서 참조한다.

## Alternatives Considered
- Controller -> Facade만 강제: 단일 도메인 유스케이스까지 과도한 추상화가 된다.
- Controller -> Service 직결만 사용: 크로스 도메인 조율 로직이 분산된다.

## Consequences
- 장점: 호출 책임이 명확해지고 리뷰 기준이 통일된다.
- 단점: 기존 문서/코드의 용어 정리가 필요하다.

