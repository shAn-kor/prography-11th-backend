# Implementation Checklist

AI가 구현 진행 시 참고하는 도메인별/계층별 체크리스트.

## 공통 규칙
- [ ] Controller는 요청/응답 변환만 수행한다.
- [ ] Facade는 여러 Application Service 조합이 필요한 경우에만 사용한다.
- [ ] Application Service 간 직접 호출을 하지 않는다.
- [ ] Repository는 Application Service에서만 참조한다.
- [ ] `@Transactional`은 Application Service에만 둔다.
- [ ] 시퀀스/요구사항/클래스/ERD 문서와 구현이 불일치하지 않는다.

## 회원(Member) 도메인

### Presentation
- [ ] `POST /auth/login` 구현
- [ ] `GET /members/{id}` 구현
- [ ] `POST /admin/members` 구현
- [ ] `GET /admin/members` 구현
- [ ] `GET /admin/members/{id}` 구현
- [ ] `PUT /admin/members/{id}` 구현
- [ ] `DELETE /admin/members/{id}` 구현
- [ ] 요청 DTO Bean Validation 적용

### Application
- [ ] 로그인 유스케이스(회원 존재/탈퇴/비밀번호 검증) 구현
- [ ] 회원 등록 유스케이스(11기 배정 + 보증금 초기화 조율) 구현
- [ ] 회원 수정/탈퇴 유스케이스 구현
- [ ] loginId 중복 처리(저장 시점 충돌 포함) 구현

### Domain
- [ ] Member 상태 전이(ACTIVE/WITHDRAWN) 규칙 반영
- [ ] 탈퇴 회원 로그인 금지 규칙 반영
- [ ] password BCrypt 처리 규칙 반영

### Infrastructure
- [ ] Member, CohortMember Repository 구현
- [ ] loginId 유니크 제약 반영
- [ ] Soft delete 조회 필터 정책 반영

## 기수/파트/팀(Cohort/Part/Team) 도메인

### Presentation
- [ ] `GET /admin/cohorts` 구현
- [ ] `GET /admin/cohorts/{id}` 구현

### Application
- [ ] 현재 운영 기수(11기) 조회 규칙 구현
- [ ] 파트/팀 유효성 검증 유스케이스 구현

### Domain
- [ ] 기수/파트/팀 관계 규칙 반영
- [ ] 운영 기수 고정 정책 반영

### Infrastructure
- [ ] Cohort/Part/Team Repository 구현
- [ ] 시드 데이터(10기/11기, 파트, 팀) 로딩 구현

## 일정(Session) 도메인

### Presentation
- [ ] `GET /sessions` 구현(회원용, CANCELLED 제외)
- [ ] `GET /admin/sessions` 구현
- [ ] `POST /admin/sessions` 구현
- [ ] `PUT /admin/sessions/{id}` 구현
- [ ] `DELETE /admin/sessions/{id}` 구현

### Application
- [ ] 일정 생성 유스케이스 구현
- [ ] 일정 수정 유스케이스 구현
- [ ] 일정 삭제(Soft delete/CANCELLED) 유스케이스 구현
- [ ] `CANCELLED` 수정 금지 규칙 구현

### Domain
- [ ] Session 상태 전이(SCHEDULED/IN_PROGRESS/COMPLETED/CANCELLED) 규칙 반영
- [ ] 회원 목록에서 `CANCELLED` 제외 규칙 반영

### Infrastructure
- [ ] Session Repository 구현
- [ ] 상태/일자 조회 인덱스 반영

## QRCode 도메인

### Presentation
- [ ] `POST /admin/sessions/{id}/qrcodes` 구현
- [ ] `PUT /admin/qrcodes/{id}` 구현

### Application
- [ ] 일정 생성 시 QR 자동 생성 조율(Session + QR) 구현
- [ ] QR 갱신(기존 즉시 만료 + 신규 생성) 유스케이스 구현

### Domain
- [ ] UUID hashValue 규칙 반영
- [ ] 24시간 TTL 규칙 반영
- [ ] 일정당 활성 QR 1개 규칙 반영

### Infrastructure
- [ ] QRCode Repository 구현
- [ ] hashValue 유니크 제약 반영

## 출결(Attendance) 도메인

### Presentation
- [ ] `POST /attendances` 구현
- [ ] `GET /attendances` 구현
- [ ] `GET /members/{id}/attendance-summary` 구현
- [ ] `POST /admin/attendances` 구현
- [ ] `PUT /admin/attendances/{id}` 구현
- [ ] `GET /admin/attendances/sessions/{id}/summary` 구현
- [ ] `GET /admin/attendances/members/{id}` 구현
- [ ] `GET /admin/attendances/sessions/{id}` 구현

### Application
- [ ] QR 출석 체크 7단계 검증 순서 구현
- [ ] 출석/지각 판정 유스케이스 구현
- [ ] 중복 출결 방지 유스케이스 구현
- [ ] 공결(EXCUSED) 최대 3회 규칙 반영
- [ ] 출결 수정 시 패널티 차이 계산 유스케이스 구현

### Domain
- [ ] 출결 상태(PRESENT/LATE/ABSENT/EXCUSED) 규칙 반영
- [ ] 지각 분 계산(초 -> 분, 버림) 규칙 반영
- [ ] 공결 카운트 증감 규칙 반영

### Infrastructure
- [ ] Attendance Repository 구현
- [ ] `(session_id, cohort_member_id)` 유니크 제약 반영

## 보증금/패널티(Deposit/Penalty) 도메인

### Presentation
- [ ] `GET /admin/cohort-members/{id}/deposits` 구현

### Application
- [ ] 회원 등록 시 초기 보증금 100,000원 설정 유스케이스 구현
- [ ] 패널티 차감 유스케이스 구현
- [ ] 출결 수정 시 환급/추가차감 유스케이스 구현
- [ ] 잔액 부족(`DEPOSIT_INSUFFICIENT`) 처리 구현

### Domain
- [ ] 패널티 정책(PRESENT 0, ABSENT 10000, LATE min(분*500,10000), EXCUSED 0) 반영
- [ ] `PenaltyCalculator` 전략 구조 반영
- [ ] 보증금 변동 타입(INITIAL/PENALTY/REFUND) 규칙 반영

### Infrastructure
- [ ] DepositHistory Repository 구현
- [ ] 잔액/이력 정합성 검증 포인트 반영

## 크로스 도메인/정합성
- [ ] 회원 등록 흐름(Member + Deposit) 실패 보상 또는 단일 트랜잭션 전략 확정
- [ ] 출석 체크 흐름(Attendance + Deposit) 실패 보상 또는 단일 트랜잭션 전략 확정
- [ ] 출결 수정 흐름(Attendance + Deposit) 실패 보상 또는 단일 트랜잭션 전략 확정
- [ ] 동시성 전략(중복 출석, 동시 차감) 적용

## 테스트
- [ ] Application Service 통합 테스트 작성
- [ ] Controller 통합 테스트 작성
- [ ] 핵심 실패 시나리오 테스트 작성(중복/만료/잔액부족/공결초과)
- [ ] 7단계 검증 순서 테스트 작성
- [ ] DB 제약조건(유니크) 테스트 작성

