package com.photography.timekeeperbackend.domain.model.attendance;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttendanceTest {

    @DisplayName("지각이 아닌 상태에 지각 분이 있으면 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void createFailsWhenLateMinutesExistsInNonLateStatus() {
        assertThatThrownBy(() -> Attendance.create(1L, 1L, AttendanceStatus.PRESENT, 1, 0, LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("패널티 금액이 음수면 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void createFailsWhenPenaltyNegative() {
        assertThatThrownBy(() -> Attendance.create(1L, 1L, AttendanceStatus.LATE, 1, -1, LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
