package com.photography.timekeeperbackend.domain.model.attendance;

import org.springframework.stereotype.Component;

/**
 * - 패널티 계산기 구현: 출결 상태별 보증금 차감 금액을 산출한다.
 */
@Component
public class DefaultPenaltyCalculator implements PenaltyCalculator {

    private static final int ABSENT_PENALTY = 10000;
    private static final int MAX_LATE_PENALTY = 10000;
    private static final int LATE_RATE_PER_MINUTE = 500;

    @Override
    public int calculate(AttendanceStatus status, int lateMinutes) {
        return switch (status) {
            case PRESENT, EXCUSED -> 0;
            case ABSENT -> ABSENT_PENALTY;
            case LATE -> Math.min(lateMinutes * LATE_RATE_PER_MINUTE, MAX_LATE_PENALTY);
        };
    }
}
