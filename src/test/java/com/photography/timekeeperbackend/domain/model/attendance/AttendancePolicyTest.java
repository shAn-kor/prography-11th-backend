package com.photography.timekeeperbackend.domain.model.attendance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendancePolicyTest {

    @DisplayName("세션 시작 이전 체크인은 PRESENT로 판정한다")
    @Test
    void checkInBeforeStartIsPresent() {
        LocalDateTime sessionStartAt = LocalDateTime.of(2026, 2, 26, 19, 0, 0);
        LocalDateTime checkedAt = LocalDateTime.of(2026, 2, 26, 18, 59, 59);

        AttendanceStatus status = AttendancePolicy.resolveStatus(sessionStartAt, checkedAt);

        assertThat(status).isEqualTo(AttendanceStatus.PRESENT);
    }

    @DisplayName("세션 시작 이후 체크인은 LATE로 판정한다")
    @Test
    void checkInAfterStartIsLate() {
        LocalDateTime sessionStartAt = LocalDateTime.of(2026, 2, 26, 19, 0, 0);
        LocalDateTime checkedAt = LocalDateTime.of(2026, 2, 26, 19, 0, 1);

        AttendanceStatus status = AttendancePolicy.resolveStatus(sessionStartAt, checkedAt);

        assertThat(status).isEqualTo(AttendanceStatus.LATE);
    }

    @DisplayName("지각 시간은 초 단위를 버림해 분으로 계산한다")
    @Test
    void lateMinutesAreFlooredBySecond() {
        LocalDateTime sessionStartAt = LocalDateTime.of(2026, 2, 26, 19, 0, 0);
        LocalDateTime checkedAt = LocalDateTime.of(2026, 2, 26, 19, 3, 59);

        int lateMinutes = AttendancePolicy.calculateLateMinutes(sessionStartAt, checkedAt);

        assertThat(lateMinutes).isEqualTo(3);
    }
}
