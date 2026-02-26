# ADR-012: 공결 카운트 책임 경계

- Status: Proposed
- Date: 2026-02-26

## Context
공결(EXCUSED) 횟수 증감은 출결 변경과 강하게 연관되지만, 멤버-기수 상태 모델과도 결합된다.

## Decision
- 공결 횟수 규칙은 CohortMember 상태 규칙으로 정의한다.
- 출결 변경 유스케이스에서 Application Service가 트랜잭션 경계 내에서 공결 증감을 조율한다.
- Repository 직접 접근으로 규칙을 우회하지 않도록 서비스 경유 규칙을 적용한다.

## Alternatives Considered
- Attendance 쪽 단독 관리: 멤버 상태와 규칙 중복 위험이 크다.
- 별도 공결 전용 도메인 분리: 현재 범위 대비 복잡도 증가.

## Consequences
- 장점: 책임 경계가 명확해지고 규칙 재사용성이 높아진다.
- 단점: 관련 서비스 간 조율 코드가 증가한다.

