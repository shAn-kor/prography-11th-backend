package com.photography.timekeeperbackend.domain.model.attendance;

/**
 * - PenaltyCalculator 포트: 출결 상태 기반 패널티 계산 규약을 정의한다.
 */
public interface PenaltyCalculator {
    int calculate(AttendanceStatus status, int lateMinutes);
}
