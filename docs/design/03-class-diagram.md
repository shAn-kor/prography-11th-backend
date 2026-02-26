# 클래스 다이어그램

## 1. 도메인 맵(대표 관계)

```mermaid
classDiagram
    direction LR

    class Member {
        +회원 계정/상태 관리
    }

    class Cohort {
        +운영 기수 관리
    }

    class Part {
        +기수별 파트 분류
    }

    class Team {
        +기수별 팀 편성
    }

    class CohortMember {
        +기수 소속/보증금/공결 횟수 관리
    }

    class Session {
        +모임 일정/상태 관리
    }

    class QRCode {
        +출석용 QR 발급/만료/갱신
    }

    class Attendance {
        +세션 출결 결과 기록
    }

    class DepositHistory {
        +보증금 변동 이력 기록
    }

    Cohort "1" <-- "*" Session
    Cohort "1" <-- "*" Part
    Cohort "1" <-- "*" Team

    Member "1" <-- "*" CohortMember
    Cohort "1" <-- "*" CohortMember
    Part "1" <-- "*" CohortMember
    Team "0..1" <-- "*" CohortMember

    Session "1" <-- "*" QRCode
    Session "1" <-- "*" Attendance
    CohortMember "1" <-- "*" Attendance
    CohortMember "1" <-- "*" DepositHistory
```

---

## 2. 도메인별 책임

### 2.1 Member / CohortMember

```mermaid
classDiagram
    class Member {
        +loginId/password/name/role/status
        +withdraw()
        +updateName(name)
        +isWithdrawn()
    }

    class CohortMember {
        +memberId/cohortId/partId/teamId
        +deposit/excuseCount
        +deductDeposit(amount)
        +refundDeposit(amount)
        +incrementExcuseCount()
        +decrementExcuseCount()
    }

    Member "1" <-- "*" CohortMember
```

### 2.2 Cohort / Part / Team

```mermaid
classDiagram
    class Cohort {
        +number/current
    }

    class Part {
        +type
    }

    class Team {
        +name
    }

    Cohort "1" <-- "*" Part
    Cohort "1" <-- "*" Team
```

### 2.3 Session / QRCode

```mermaid
classDiagram
    class Session {
        +cohort
        +title/description/type/status/sessionDate
        +update(...)
        +cancel()
        +updateStatus(status)
        +isCancelled()
    }

    class QRCode {
        +sessionId/hashValue/expiresAt
        +issue(sessionId, now)
        +isExpired(now)
        +expireNow(now)
    }

    Session "1" <-- "*" QRCode
```

### 2.4 Attendance / Deposit

```mermaid
classDiagram
    class Attendance {
        +sessionId/cohortMemberId
        +status/lateMinutes/penaltyAmount/checkedAt
        +update(status, lateMinutes, penaltyAmount)
    }

    class AttendancePolicy {
        +resolveStatus(sessionDate, checkedAt)
        +calculateLateMinutes(sessionDate, checkedAt)
    }

    class DepositHistory {
        +cohortMemberId/type/amount/balanceAfter/description/createdAt
    }

    class PenaltyCalculator {
        <<interface>>
        +calculate(status, lateMinutes)
    }

    class DefaultPenaltyCalculator {
        +calculate(status, lateMinutes)
    }

    PenaltyCalculator <|.. DefaultPenaltyCalculator
```

---

## 3. 문서 원칙

- 이 문서는 도메인 모델의 **관계와 책임**만 다룬다.
- `Controller`, `Facade`, `Repository` 같은 레이어 구성요소는 포함하지 않는다.
- 구현 상세(트랜잭션, SQL, 프레임워크 어노테이션 흐름)는 다른 설계 문서에서 다룬다.
