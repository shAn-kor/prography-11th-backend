package com.photography.timekeeperbackend.domain.model.attendance;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * - Attendance 정책: 체크 시각을 기반으로 출결 상태와 지각 분을 계산한다.
 */
public final class AttendancePolicy {

    private AttendancePolicy() {
    }

    public static AttendanceStatus resolveStatus(LocalDateTime sessionStartAt, LocalDateTime checkedAt) {
        if (checkedAt.isAfter(sessionStartAt)) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.PRESENT;
    }

    public static int calculateLateMinutes(LocalDateTime sessionStartAt, LocalDateTime checkedAt) {
        if (!checkedAt.isAfter(sessionStartAt)) {
            return 0;
        }
        long seconds = Duration.between(sessionStartAt, checkedAt).getSeconds();
        return (int) (seconds / 60);
    }
}
