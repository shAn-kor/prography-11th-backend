# ADR-007: QR 수명주기 정책

- Status: Proposed
- Date: 2026-02-26

## Context
출석 체크의 핵심 진입점은 QR 코드이며, 재발급/중복 사용/만료 정책이 명확해야 한다.

## Decision
- QR은 UUID 기반 hashValue를 사용한다.
- 유효기간은 생성 시점부터 24시간이다.
- 일정당 활성 QR은 1개만 허용한다.
- QR 갱신 시 기존 QR을 즉시 만료하고 새 QR을 발급한다.

## Alternatives Considered
- 다중 활성 QR 허용: 운영 편의는 있지만 오사용 위험이 커진다.
- 긴 TTL: 재사용 공격면이 커진다.

## Consequences
- 장점: 운영 규칙이 단순하고 검증 가능하다.
- 단점: 갱신 타이밍 관리 책임이 운영자에게 일부 남는다.

