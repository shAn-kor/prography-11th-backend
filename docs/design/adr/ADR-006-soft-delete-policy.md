# ADR-006: Soft Delete 정책

- Status: Proposed
- Date: 2026-02-26

## Context
Member와 Session은 삭제 대신 상태 전환이 요구된다. 조회 API에서 삭제 데이터 노출 정책이 필요하다.

## Decision
- Member는 `WITHDRAWN`, Session은 `CANCELLED` 상태를 사용한다.
- 기본 조회는 삭제 상태를 제외한다.
- 관리자/감사 조회처럼 필요한 경우만 명시적 메서드로 포함 조회한다.

## Alternatives Considered
- Hard Delete: 이력 추적성과 감사 가능성이 떨어진다.
- 전역 필터 강제: 예외 조회 케이스 관리가 어려워질 수 있다.

## Consequences
- 장점: 이력 보존과 운영 안정성이 높다.
- 단점: 쿼리/인덱스/조회 규칙 관리가 추가된다.

