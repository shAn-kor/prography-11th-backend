package com.photography.timekeeperbackend.domain.model.attendance;

import com.photography.timekeeperbackend.domain.model.common.BaseTimeEntity;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * - Attendance 엔티티: 한 세션에서 한 기수회원의 출결 결과를 기록한다.
 */
@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_session_cohort_member", columnNames = {"session_id", "cohort_member_id"})
})
@Getter
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "cohort_member_id", nullable = false)
    private Long cohortMemberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(nullable = false)
    private int lateMinutes;

    @Column(nullable = false)
    private int penaltyAmount;

    @Column
    private LocalDateTime checkedAt;

    protected Attendance() {
    }

    public static Attendance create(Long sessionId, Long cohortMemberId, AttendanceStatus status,
                                    int lateMinutes, int penaltyAmount, LocalDateTime checkedAt) {
        validateIdentity(sessionId, "sessionId");
        validateIdentity(cohortMemberId, "cohortMemberId");
        validateStatusAndTime(status, lateMinutes);
        validatePenalty(penaltyAmount);
        if (checkedAt == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "출결 체크 시각은 필수입니다.");
        }
        Attendance attendance = new Attendance();
        attendance.sessionId = sessionId;
        attendance.cohortMemberId = cohortMemberId;
        attendance.status = status;
        attendance.lateMinutes = lateMinutes;
        attendance.penaltyAmount = penaltyAmount;
        attendance.checkedAt = checkedAt;
        return attendance;
    }

    public void update(AttendanceStatus status, int lateMinutes, int penaltyAmount) {
        validateStatusAndTime(status, lateMinutes);
        validatePenalty(penaltyAmount);
        this.status = status;
        this.lateMinutes = lateMinutes;
        this.penaltyAmount = penaltyAmount;
    }

    private static void validateIdentity(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, fieldName + "는 1 이상이어야 합니다.");
        }
    }

    private static void validateStatusAndTime(AttendanceStatus status, int lateMinutes) {
        if (status == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "출결 상태는 필수입니다.");
        }
        if (lateMinutes < 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "지각 분은 0 이상이어야 합니다.");
        }
        if (status != AttendanceStatus.LATE && lateMinutes != 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "지각 상태가 아니면 지각 분은 0이어야 합니다.");
        }
    }

    private static void validatePenalty(int penaltyAmount) {
        if (penaltyAmount < 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "패널티 금액은 0 이상이어야 합니다.");
        }
    }
}
