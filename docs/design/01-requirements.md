# 프로그라피 출결관리 Backend 요구사항 명세서

## 1. 개요

### 1.1 프로젝트 핵심 가치
> QR 기반 출석 체크와 자동 패널티/보증금 연동으로 프로그라피 출결 관리를 자동화하는 시스템

### 1.2 문제 정의

| 관점 | 문제 |
|------|------|
| 사용자 관점 | 정기 모임 출결을 수기로 관리하면 번거롭고 오류 발생. 보증금 차감 계산 번거로움 |
| 비즈니스 관점 | IT 동아리 운영 효율화, 출결 기록 투명성, 보증금 패널티로 참여 동기 부여 |
| 시스템 관점 | 출결-보증금 데이터 일관성 유지, QR 기반 실시간 출석 체크, 패널티 자동 계산 |

### 1.3 기술 스택
- Backend: Spring Boot 3.x, JDK 21
- Database: H2 (In-memory)
- Security: BCrypt (비밀번호 해싱만, 인증/인가 없음)

### 1.4 아키텍처 레이어 구조

```
Presentation → Application → Domain ← Infrastructure
```

| 레이어 | 책임 | 주요 컴포넌트 |
|--------|------|--------------|
| **Presentation** | HTTP 요청/응답 처리 | Controller, DTO |
| **Application** | 유스케이스 실행, 트랜잭션 경계, 크로스 도메인 조율 | Facade, Application Service |
| **Domain** | 순수 비즈니스 규칙과 모델 | Entity, VO, Domain Policy |
| **Infrastructure** | 영속성, 외부 시스템 연동 | Repository, Client |

**레이어 규칙:**
- Controller는 요청/응답 변환만 담당하고, Facade 또는 Application Service를 호출
- Facade는 여러 Application Service 조합이 필요한 경우에만 사용
- Application Service 간 직접 호출 금지 (크로스 도메인 협력은 Facade 경유)
- Application Service는 자기 도메인의 Repository에만 접근
- Domain은 순수 비즈니스 규칙만 담당 (저장/외부 I/O/트랜잭션 금지)
- `@Transactional`은 Application Service에만 위치 (Facade/Domain 금지)

---

## 2. 용어 사전

| 용어 | 정의 |
|------|------|
| Member | 회원. loginId, password, name 등 기본 정보 |
| Cohort | 기수. 예: 10기, 11기 |
| Part | 파트. SERVER, WEB, iOS, ANDROID, DESIGN |
| Team | 팀. 예: Team A, Team B, Team C |
| CohortMember | 기수별 회원 정보. 보증금, 파트, 팀 배정 |
| Session | 정기 모임 일정 |
| QRCode | 출석용 QR 코드. UUID hashValue, 24시간 유효 |
| Attendance | 출결 기록. PRESENT, LATE, ABSENT, EXCUSED |
| Deposit | 보증금. 초기 100,000원 |
| DepositHistory | 보증금 변동 이력. INITIAL, PENALTY, REFUND |

---

## 3. 핵심 도메인

| # | 도메인 | 설명 |
|---|--------|------|
| 1 | 회원 관리 | 등록/수정/탈퇴, 로그인 |
| 2 | 기수/파트/팀 | 조직 구조 (시드 데이터) |
| 3 | 일정 관리 | 정기 모임 일정 CRUD |
| 4 | QR 코드 | 출석용 QR 생성/갱신 |
| 5 | 출결 관리 | QR 출석 체크, 출결 등록/수정 |
| 6 | 보증금/패널티 | 자동 차감/환급, 이력 관리 |

---

## 4. 비즈니스 규칙

### 4.1 회원 (Member)

| ID | 규칙 |
|----|------|
| MEM-01 | loginId는 시스템 전체에서 유니크 (탈퇴 회원 포함) |
| MEM-02 | password는 BCrypt로 해싱 |
| MEM-03 | 회원 등록 시 11기에 자동 배정 + 보증금 100,000원 자동 설정 |
| MEM-04 | 회원 탈퇴는 Soft delete (WITHDRAWN 상태) |
| MEM-05 | 현재 운영 기수는 11기 고정 |
| MEM-06 | 탈퇴(WITHDRAWN) 회원은 로그인 불가 |

### 4.2 일정 (Session)

| ID | 규칙 |
|----|------|
| SES-01 | 일정 생성 시 QR 코드 자동 생성 |
| SES-02 | 일정 삭제는 Soft delete (CANCELLED 상태) |
| SES-03 | CANCELLED 상태의 일정은 수정 불가 |
| SES-04 | 회원용 일정 목록에서 CANCELLED 제외 |
| SES-05 | 일정 상태는 관리자가 API로 수동 변경 |

### 4.3 QR 코드 (QRCode)

| ID | 규칙 |
|----|------|
| QR-01 | UUID 기반 hashValue |
| QR-02 | 유효기간: 생성 시점부터 24시간 |
| QR-03 | 하나의 일정에 활성(미만료) QR 코드는 1개만 존재 |
| QR-04 | QR 갱신 시 기존 QR 즉시 만료 + 새 QR 생성 |

### 4.4 출결 (Attendance)

| ID | 규칙 |
|----|------|
| ATT-01 | 출석/지각 판정: 일정 시작 시간 이전=PRESENT, 이후=LATE |
| ATT-02 | 지각 시간 계산: 초 단위 → 분으로 변환 (버림) |
| ATT-03 | 중복 출결 불가 (동일 일정 + 동일 회원) |
| ATT-04 | 공결(EXCUSED): 기수당 최대 3회 |
| ATT-05 | 출결 수정 시 패널티 차이만큼 보증금 자동 조정 |

### 4.5 패널티

**현재 구현 (과제 요구사항):**

| 출결 상태 | 패널티 |
|----------|--------|
| PRESENT | 0원 |
| ABSENT | 10,000원 |
| LATE | min(지각분 × 500, 10,000)원 |
| EXCUSED | 0원 |

**설계 고려사항 (확장성):**

패널티 정책은 비즈니스 요구에 따라 자주 변경될 수 있는 영역입니다. 다음과 같은 확장을 고려하여 **변경에 닫히고 확장에 열린 구조(OCP)** 로 설계합니다:

| 확장 시나리오 | 예시 |
|--------------|------|
| 단위 변경 | 초당 X원, 시간당 Y원 |
| 할증 정책 | 30분 초과 시 2배 할증 |
| 구간별 요금 | 1-10분: 500원/분, 11-20분: 1,000원/분 |
| 최소/최대 패널티 | 최소 1,000원, 최대 50,000원 |
| 시간대별 차등 | 오전 세션 vs 오후 세션 |

**권장 구현 패턴:**
- `PenaltyCalculator` 인터페이스 + 전략 패턴 적용
- 정책 파라미터를 설정(Config)으로 분리
- 현재 구현은 `DefaultPenaltyCalculator`로 분당 500원 고정

### 4.6 보증금 (Deposit)

| ID | 규칙 |
|----|------|
| DEP-01 | 초기 보증금: 100,000원 (회원 등록 시 자동) |
| DEP-02 | 패널티 발생 시 보증금에서 차감 |
| DEP-03 | 보증금 잔액보다 큰 패널티는 차감 불가 (DEPOSIT_INSUFFICIENT) |
| DEP-04 | 모든 보증금 변동은 이력(DepositHistory)에 기록 |
| DEP-05 | 이력 유형: INITIAL(초기), PENALTY(차감), REFUND(환급) |

---

## 5. QR 출석 체크 검증 순서

아래 순서대로 검증합니다. 실패 시 해당 에러를 반환합니다.

| 순서 | 검증 항목 | 에러 코드 |
|------|----------|----------|
| 1 | QR hashValue 유효성 | QR_INVALID |
| 2 | QR 만료 여부 | QR_EXPIRED |
| 3 | 일정 상태가 IN_PROGRESS | SESSION_NOT_IN_PROGRESS |
| 4 | 회원 존재 여부 | MEMBER_NOT_FOUND |
| 5 | 회원 탈퇴 여부 | MEMBER_WITHDRAWN |
| 6 | 중복 출결 여부 | ATTENDANCE_ALREADY_CHECKED |
| 7 | 기수 회원 정보 존재 여부 | COHORT_MEMBER_NOT_FOUND |

---

## 6. API 명세

### 6.1 필수 API (16개)

| # | Method | Path | 설명 |
|---|--------|------|------|
| 1 | POST | /auth/login | 로그인 |
| 2 | GET | /members/{id} | 회원 조회 |
| 3 | POST | /admin/members | 회원 등록 |
| 4 | GET | /admin/members | 회원 대시보드 |
| 5 | GET | /admin/members/{id} | 회원 상세 |
| 6 | PUT | /admin/members/{id} | 회원 수정 |
| 7 | DELETE | /admin/members/{id} | 회원 탈퇴 |
| 8 | GET | /admin/cohorts | 기수 목록 |
| 9 | GET | /admin/cohorts/{id} | 기수 상세 |
| 10 | GET | /sessions | 일정 목록 (회원) |
| 11 | GET | /admin/sessions | 일정 목록 (관리자) |
| 12 | POST | /admin/sessions | 일정 생성 |
| 13 | PUT | /admin/sessions/{id} | 일정 수정 |
| 14 | DELETE | /admin/sessions/{id} | 일정 삭제 |
| 15 | POST | /admin/sessions/{id}/qrcodes | QR 생성 |
| 16 | PUT | /admin/qrcodes/{id} | QR 갱신 |

### 6.2 가산점 API (9개)

| # | Method | Path | 설명 |
|---|--------|------|------|
| 17 | POST | /attendances | QR 출석 체크 |
| 18 | GET | /attendances | 내 출결 기록 |
| 19 | GET | /members/{id}/attendance-summary | 내 출결 요약 |
| 20 | POST | /admin/attendances | 출결 등록 |
| 21 | PUT | /admin/attendances/{id} | 출결 수정 |
| 22 | GET | /admin/attendances/sessions/{id}/summary | 일정별 출결 요약 |
| 23 | GET | /admin/attendances/members/{id} | 회원 출결 상세 |
| 24 | GET | /admin/attendances/sessions/{id} | 일정별 출결 목록 |
| 25 | GET | /admin/cohort-members/{id}/deposits | 보증금 이력 |

---

## 7. 시드 데이터

| 데이터 | 내용 |
|--------|------|
| 기수 | 10기, 11기 |
| 파트 | 기수별 SERVER, WEB, iOS, ANDROID, DESIGN (총 10개) |
| 팀 | 11기 Team A, Team B, Team C (총 3개) |
| 관리자 | loginId: `admin`, password: `admin1234`, role: ADMIN |
| 보증금 | 관리자 초기 보증금 100,000원 |

---

## 8. User Stories

### US-AUTH-01: 로그인
**As a** 회원
**I want to** loginId와 password로 로그인하여
**So that** 본인 인증을 할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 올바른 loginId/password, When 로그인 요청, Then 성공 응답 (회원 정보 반환)
- [ ] AC2: Given 잘못된 loginId, When 로그인 요청, Then 에러 응답
- [ ] AC3: Given 잘못된 password, When 로그인 요청, Then 에러 응답
- [ ] AC4: Given 탈퇴(WITHDRAWN) 회원, When 로그인 요청, Then 에러 응답

---

### US-MEM-01: 회원 등록
**As a** 관리자
**I want to** 새 회원을 등록하여
**So that** 출결 관리 대상에 포함시킬 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 유효한 회원 정보, When 등록 요청, Then 회원 생성 + 11기 배정 + 보증금 100,000원 설정 + DepositHistory(INITIAL) 생성
- [ ] AC2: Given 중복된 loginId, When 등록 요청, Then 에러 응답
- [ ] AC3: Given 존재하지 않는 파트/팀, When 등록 요청, Then 에러 응답

---

### US-MEM-02: 회원 탈퇴
**As a** 관리자
**I want to** 회원을 탈퇴 처리하여
**So that** 더 이상 출결 관리 대상에서 제외할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 활성 회원, When 탈퇴 요청, Then 상태를 WITHDRAWN으로 변경
- [ ] AC2: Given 이미 탈퇴한 회원, When 탈퇴 요청, Then 에러 응답

---

### US-SES-01: 일정 생성
**As a** 관리자
**I want to** 정기 모임 일정을 생성하여
**So that** 출결 관리를 시작할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 유효한 일정 정보, When 생성 요청, Then 일정 생성 + QR 코드 자동 생성

---

### US-SES-02: 일정 삭제
**As a** 관리자
**I want to** 일정을 삭제하여
**So that** 취소된 모임을 관리할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 활성 일정, When 삭제 요청, Then 상태를 CANCELLED로 변경
- [ ] AC2: Given CANCELLED 상태의 일정은 회원용 목록에서 제외

---

### US-QR-01: QR 갱신
**As a** 관리자
**I want to** QR 코드를 갱신하여
**So that** 새로운 출석 체크 수단을 제공할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 유효한 QR, When 갱신 요청, Then 기존 QR 만료 + 새 QR 생성

---

### US-ATT-01: QR 출석 체크
**As a** 회원
**I want to** QR 코드를 스캔하여
**So that** 출석 체크를 할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 유효한 QR + IN_PROGRESS 일정 + 시작 시간 이전, When 출석 요청, Then PRESENT 기록
- [ ] AC2: Given 유효한 QR + IN_PROGRESS 일정 + 시작 시간 이후, When 출석 요청, Then LATE 기록 + 패널티 계산 + 보증금 차감
- [ ] AC3: Given 만료된 QR, When 출석 요청, Then QR_EXPIRED 에러
- [ ] AC4: Given 이미 출석한 회원, When 출석 요청, Then ATTENDANCE_ALREADY_CHECKED 에러

---

### US-ATT-02: 출결 수정
**As a** 관리자
**I want to** 출결 상태를 수정하여
**So that** 잘못된 출결을 정정할 수 있다

**수용 기준 (AC):**
- [ ] AC1: Given 기존 출결, When LATE→PRESENT로 수정, Then 패널티 차이만큼 보증금 환급
- [ ] AC2: Given 기존 출결, When PRESENT→LATE로 수정, Then 패널티 차이만큼 보증금 차감
- [ ] AC3: Given 기존 출결, When EXCUSED로 수정 + 공결 3회 초과, Then EXCUSE_LIMIT_EXCEEDED 에러
- [ ] AC4: Given 보증금 잔액 부족, When 패널티 증가 수정, Then DEPOSIT_INSUFFICIENT 에러

---

## 부록: 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0 | 2026-02-16 | 초안 작성 |
