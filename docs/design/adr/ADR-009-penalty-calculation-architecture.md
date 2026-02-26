# ADR-009: 패널티 계산 아키텍처

- Status: Proposed
- Date: 2026-02-26

## Context
패널티 정책은 고정 규칙으로 시작하지만 변경 가능성이 높은 영역이다.

## Decision
- `PenaltyCalculator` 전략 인터페이스를 사용한다.
- 기본 정책은 `DefaultPenaltyCalculator`로 구현한다.
- 정책 변경 대비를 위해 설정 기반 파라미터화를 허용한다.
- 필요 시 정책 버전 필드를 Attendance에 추가하는 방안을 유지한다.

## Alternatives Considered
- if/else 단일 구현: 초기 단순하지만 정책 확장 시 변경 비용이 높다.
- 룰 엔진 도입: MVP 대비 과설계 가능성이 높다.

## Consequences
- 장점: 정책 변경 영향이 계산 컴포넌트로 국소화된다.
- 단점: 초기 구조가 단순 구현보다 복잡하다.

