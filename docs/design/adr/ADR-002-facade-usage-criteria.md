# ADR-002: Facade 사용 기준

- Status: Proposed
- Date: 2026-02-26

## Context
모든 유스케이스에 Facade를 도입하면 구조가 비대해지고, 미도입 시 크로스 도메인 조율이 흩어진다.

## Decision
- Facade는 둘 이상의 Application Service 조합이 필요한 경우에만 도입한다.
- 단일 도메인 유스케이스는 Controller -> Application Service로 직접 호출한다.
- Facade는 Repository를 직접 참조하지 않는다.

## Alternatives Considered
- 항상 Facade 사용: 단순 기능에서도 불필요한 계층이 생긴다.
- Facade 미사용: 크로스 도메인 보상/순서 제어가 어려워진다.

## Consequences
- 장점: 단순 유스케이스는 단순하게, 복합 유스케이스는 일관되게 관리 가능.
- 단점: 어떤 케이스가 "복합"인지 합의 기준이 필요하다.

