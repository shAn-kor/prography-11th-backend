package com.photography.timekeeperbackend.domain.model.attendance;

/**
 * - Attendance 상태: 출석 처리 결과의 표준 상태값을 정의한다.
 */
public enum AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    EXCUSED
}
