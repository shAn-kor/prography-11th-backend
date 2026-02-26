# ADR-010: 보증금 정합성 모델

- Status: Proposed
- Date: 2026-02-26

## Context
`CohortMember.deposit` 잔액과 `DepositHistory` 누적 합계가 불일치할 가능성이 있다.

## Decision
- MVP는 `현재 잔액 + 변경 이력` 동시 관리 모델을 유지한다.
- 모든 잔액 변경은 반드시 `DepositHistory`를 함께 기록한다.
- 정합성 검증을 위한 점검 쿼리/배치를 운영 항목으로 둔다.

## Alternatives Considered
- 이력만 저장하고 잔액은 매 조회 계산: 정합성은 좋지만 조회 비용이 커진다.
- 잔액만 저장: 감사/추적 가능성이 떨어진다.

## Consequences
- 장점: 조회 성능과 감사 추적 사이의 균형을 맞춘다.
- 단점: 정합성 점검 프로세스를 반드시 운영해야 한다.

