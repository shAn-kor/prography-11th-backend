package com.photography.timekeeperbackend.domain.model.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * - Attendance 도메인 서비스: 출결 상태 변경 시 지각 분/패널티 계산과 상태 반영을 담당한다.
 */
@Service
@RequiredArgsConstructor
public class AttendanceDomainService {

    private final PenaltyCalculator penaltyCalculator;

    public int updateStatusAndGetPenaltyDiff(Attendance attendance, AttendanceStatus newStatus) {
        int oldPenalty = attendance.getPenaltyAmount();
        int newLateMinutes = newStatus == AttendanceStatus.LATE ? attendance.getLateMinutes() : 0;
        int newPenalty = penaltyCalculator.calculate(newStatus, newLateMinutes);
        attendance.update(newStatus, newLateMinutes, newPenalty);
        return newPenalty - oldPenalty;
    }

    public Attendance createByQr(Long sessionId, Long cohortMemberId, LocalDateTime sessionDate) {
        LocalDateTime checkedAt = LocalDateTime.now();
        AttendanceStatus status = AttendancePolicy.resolveStatus(sessionDate, checkedAt);
        int lateMinutes = AttendancePolicy.calculateLateMinutes(sessionDate, checkedAt);
        int penalty = penaltyCalculator.calculate(status, lateMinutes);
        return Attendance.create(sessionId, cohortMemberId, status, lateMinutes, penalty, checkedAt);
    }

    public Attendance createManual(Long sessionId, Long cohortMemberId, AttendanceStatus status) {
        int lateMinutes = status == AttendanceStatus.LATE ? 1 : 0;
        int penalty = penaltyCalculator.calculate(status, lateMinutes);
        return Attendance.create(sessionId, cohortMemberId, status, lateMinutes, penalty, LocalDateTime.now());
    }
}
