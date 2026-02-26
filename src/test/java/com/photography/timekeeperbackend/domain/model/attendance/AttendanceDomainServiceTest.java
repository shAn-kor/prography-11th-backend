package com.photography.timekeeperbackend.domain.model.attendance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceDomainServiceTest {

    @Mock
    private PenaltyCalculator penaltyCalculator;

    @DisplayName("출결 상태 변경 시 패널티 차이를 반환하고 상태를 갱신한다")
    @Test
    void updateStatusAndGetPenaltyDiffUpdatesAttendance() {
        AttendanceDomainService service = new AttendanceDomainService(penaltyCalculator);
        Attendance attendance = Attendance.create(1L, 2L, AttendanceStatus.ABSENT, 0, 10000, LocalDateTime.now());
        when(penaltyCalculator.calculate(AttendanceStatus.EXCUSED, 0)).thenReturn(0);

        int diff = service.updateStatusAndGetPenaltyDiff(attendance, AttendanceStatus.EXCUSED);

        assertThat(diff).isEqualTo(-10000);
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.EXCUSED);
        assertThat(attendance.getLateMinutes()).isZero();
        assertThat(attendance.getPenaltyAmount()).isZero();
    }

    @DisplayName("수동 생성 시 LATE 상태는 lateMinutes=1로 생성한다")
    @Test
    void createManualUsesOneMinuteForLate() {
        AttendanceDomainService service = new AttendanceDomainService(penaltyCalculator);
        when(penaltyCalculator.calculate(AttendanceStatus.LATE, 1)).thenReturn(500);

        Attendance attendance = service.createManual(10L, 20L, AttendanceStatus.LATE);

        assertThat(attendance.getSessionId()).isEqualTo(10L);
        assertThat(attendance.getCohortMemberId()).isEqualTo(20L);
        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(attendance.getLateMinutes()).isEqualTo(1);
        assertThat(attendance.getPenaltyAmount()).isEqualTo(500);
    }

    @DisplayName("QR 생성 시 세션 시간이 미래면 PRESENT로 생성한다")
    @Test
    void createByQrBeforeStartCreatesPresent() {
        AttendanceDomainService service = new AttendanceDomainService(penaltyCalculator);
        LocalDateTime sessionDate = LocalDateTime.now().plusMinutes(1);
        when(penaltyCalculator.calculate(AttendanceStatus.PRESENT, 0)).thenReturn(0);

        Attendance attendance = service.createByQr(10L, 20L, sessionDate);

        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(attendance.getLateMinutes()).isZero();
        assertThat(attendance.getPenaltyAmount()).isZero();
    }

    @DisplayName("QR 생성 시 세션 시간이 과거면 LATE로 생성한다")
    @Test
    void createByQrAfterStartCreatesLate() {
        AttendanceDomainService service = new AttendanceDomainService(penaltyCalculator);
        LocalDateTime sessionDate = LocalDateTime.now().minusMinutes(5);
        when(penaltyCalculator.calculate(eq(AttendanceStatus.LATE), anyInt())).thenReturn(2500);

        Attendance attendance = service.createByQr(10L, 20L, sessionDate);

        assertThat(attendance.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(attendance.getLateMinutes()).isGreaterThanOrEqualTo(5);
        assertThat(attendance.getPenaltyAmount()).isEqualTo(2500);
    }
}
