# ADR-003: 트랜잭션 경계 정책

- Status: Proposed
- Date: 2026-02-26

## Context
트랜잭션 위치가 혼재되면 일관성 이슈와 코드 리뷰 혼선이 발생한다.

## Decision
- `@Transactional`은 Application Service에만 둔다.
- Facade는 트랜잭션 시작 지점이 아니다.
- Domain Model은 트랜잭션/영속성 기술에 의존하지 않는다.

## Alternatives Considered
- Facade 트랜잭션: 오케스트레이션 레이어가 인프라 책임까지 갖게 된다.
- Domain Service 트랜잭션: 도메인 순수성 원칙과 충돌한다.

## Consequences
- 장점: 트랜잭션 경계가 명확해지고 테스트 전략 수립이 쉬워진다.
- 단점: 기존 코드 이동/리팩터링 비용이 발생할 수 있다.

