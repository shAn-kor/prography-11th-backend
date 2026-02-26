# ADR-011: 동시성 제어 전략

- Status: Proposed
- Date: 2026-02-26

## Context
QR 중복 출석과 동시 보증금 차감은 실제 운영에서 발생 가능한 경쟁 조건이다.

## Decision
- 출결 중복은 DB 유니크 제약(`session_id`, `cohort_member_id`)으로 1차 방어한다.
- 보증금 차감은 락 전략(비관적 락 우선 검토, 필요 시 낙관적 락)으로 보호한다.
- Application Service에서 재시도/예외 변환 정책을 명시한다.

## Alternatives Considered
- 애플리케이션 레벨 검사만 사용: 레이스 컨디션을 막지 못한다.
- 무조건 비관적 락: 안전하지만 처리량 저하 가능성이 있다.

## Consequences
- 장점: 데이터 무결성 보호 수준이 높아진다.
- 단점: 락 경합 구간에서 성능 튜닝이 필요하다.

