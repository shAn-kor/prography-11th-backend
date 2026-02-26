# 시퀀스 다이어그램

> 이 문서의 `*Service`는 Spring 레이어 기준의 Application Service를 의미한다.

## 1. 로그인

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor User as 회원
    participant Controller as AuthController
    participant Service as MemberService

    User->>+Controller: POST /auth/login (loginId, password)
    Controller->>+Service: 로그인 처리 요청
    Service-->>-Controller: 결과 (성공/실패)
    Controller-->>-User: 응답

    Note over Service: 검증 순서: 회원존재 → 탈퇴여부 → 비밀번호
```

### 핵심 포인트
- MemberService가 회원 조회 + 상태 검증 + 비밀번호 검증 책임
- BCrypt 검증은 MemberService 내부에서 처리

### 설계 리스크
- 없음 (단순 조회 로직)

---

## 2. 회원 등록

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor Admin as 관리자
    participant Controller as MemberController
    participant Facade as MemberFacade
    participant MemberSvc as MemberService
    participant DepositSvc as DepositService

    Admin->>+Controller: POST /admin/members (회원 정보)
    Controller->>+Facade: 회원 등록 요청

    Facade->>+MemberSvc: loginId 중복 검사
    MemberSvc-->>-Facade: 결과

    Facade->>+MemberSvc: 회원 + 기수회원 생성 (11기)
    MemberSvc-->>-Facade: Member, CohortMember

    Facade->>+DepositSvc: 초기 보증금 설정 (100,000원)
    DepositSvc-->>-Facade: DepositHistory (INITIAL)

    Facade-->>-Controller: 회원 정보
    Controller-->>-Admin: 201 Created

    Note over Facade: 중복 시 LOGIN_ID_DUPLICATED 에러 반환
```

### 핵심 포인트
- Facade가 MemberService와 DepositService를 조율
- 트랜잭션 경계는 각 Application Service에서 관리
- 실패 시 Facade에서 보상 로직 처리 가능

### 설계 리스크
- 크로스 도메인 트랜잭션: Member 생성 후 Deposit 실패 시 보상 필요

---

## 3. 일정 생성

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor Admin as 관리자
    participant Controller as SessionController
    participant Facade as SessionFacade
    participant SessionSvc as SessionService
    participant QRSvc as QRCodeService

    Admin->>+Controller: POST /admin/sessions (일정 정보)
    Controller->>+Facade: 일정 생성 요청

    Facade->>+SessionSvc: 일정 생성
    SessionSvc-->>-Facade: Session

    Facade->>+QRSvc: QR 코드 생성 (24시간 유효)
    QRSvc-->>-Facade: QRCode

    Facade-->>-Controller: Session + QRCode
    Controller-->>-Admin: 201 Created
```

### 핵심 포인트
- SessionService는 일정만, QRCodeService는 QR만 담당
- Facade가 두 Application Service를 조율

### 설계 리스크
- 없음 (단순 생성 로직)

---

## 4. QR 갱신

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor Admin as 관리자
    participant Controller as QRCodeController
    participant Service as QRCodeService

    Admin->>+Controller: PUT /admin/qrcodes/{id}
    Controller->>+Service: QR 갱신 요청
    Service-->>-Controller: 새 QRCode
    Controller-->>-Admin: 200 OK

    Note over Service: QR 없으면 QR_NOT_FOUND 에러
```

### 핵심 포인트
- 단일 도메인이므로 Facade 없이 Application Service 직접 호출
- 기존 QR 만료 + 새 QR 생성은 QRCodeService 트랜잭션 경계 내에서 처리

### 설계 리스크
- 없음 (단일 도메인 처리)

---

## 5. QR 출석 체크

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor User as 회원
    participant Controller as AttendanceController
    participant Facade as AttendanceFacade
    participant QRSvc as QRCodeService
    participant SessionSvc as SessionService
    participant MemberSvc as MemberService
    participant AttSvc as AttendanceService
    participant DepositSvc as DepositService

    User->>+Controller: POST /attendances (hashValue, memberId)
    Controller->>+Facade: 출석 체크 요청

    Note over Facade: 검증 순서 (실패 시 즉시 에러 반환)

    Facade->>+QRSvc: 1-2. QR 검증 (유효성 + 만료)
    QRSvc-->>-Facade: QRCode (sessionId 포함)

    Facade->>+SessionSvc: 3. 일정 상태 확인 (IN_PROGRESS)
    SessionSvc-->>-Facade: Session

    Facade->>+MemberSvc: 4-5-7. 회원 검증 (존재 + 탈퇴 + 기수회원)
    MemberSvc-->>-Facade: Member, CohortMember

    Facade->>+AttSvc: 6. 중복 출결 확인
    AttSvc-->>-Facade: OK

    Facade->>+AttSvc: 출석 판정 및 저장
    AttSvc-->>-Facade: Attendance (패널티 금액 포함)

    Facade->>+DepositSvc: 보증금 차감 (패널티 > 0인 경우)
    DepositSvc-->>-Facade: DepositHistory (PENALTY)

    Facade-->>-Controller: Attendance
    Controller-->>-User: 201 Created

    Note over DepositSvc: 잔액 부족 시 DEPOSIT_INSUFFICIENT 에러
```

### 핵심 포인트
- Facade가 5개 Application Service를 조율 (QR, Session, Member, Attendance, Deposit)
- 각 Application Service는 자기 도메인 검증만 담당
- 패널티 계산은 AttendanceService 내 PenaltyCalculator가 담당 (전략 패턴)
- 보증금 차감은 DepositService가 담당

### 설계 리스크
- 크로스 도메인 트랜잭션: Attendance 저장 후 Deposit 차감 실패 시 보상 필요
- 검증 순서가 비즈니스 요구사항이므로 순서 변경 시 영향 분석 필요

---

## 6. 출결 수정 (보증금 자동 조정)

### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    actor Admin as 관리자
    participant Controller as AttendanceController
    participant Facade as AttendanceFacade
    participant AttSvc as AttendanceService
    participant DepositSvc as DepositService

    Admin->>+Controller: PUT /admin/attendances/{id} (newStatus)
    Controller->>+Facade: 출결 수정 요청

    Facade->>+AttSvc: 기존 출결 조회 + 패널티 차이 계산
    AttSvc-->>-Facade: 패널티 차이 금액

    Facade->>+DepositSvc: 보증금 조정 (차감 또는 환급)
    DepositSvc-->>-Facade: DepositHistory

    Facade->>+AttSvc: 출결 상태 업데이트
    AttSvc-->>-Facade: Attendance

    Facade-->>-Controller: Attendance
    Controller-->>-Admin: 200 OK

    Note over AttSvc: EXCUSED 3회 초과 시 EXCUSE_LIMIT_EXCEEDED
    Note over DepositSvc: 잔액 부족 시 DEPOSIT_INSUFFICIENT
```

### 핵심 포인트
- AttendanceService가 패널티 차이 계산 담당
- DepositService가 보증금 차감/환급 담당
- Facade가 두 Application Service를 조율하며 보상 로직 처리

### 설계 리스크
- 출결 상태 업데이트와 보증금 조정이 다른 트랜잭션일 경우 불일치 가능
- 공결 횟수 조정은 Facade에서 MemberService와 AttendanceService를 함께 조율하는 흐름으로 분리 검토 필요
