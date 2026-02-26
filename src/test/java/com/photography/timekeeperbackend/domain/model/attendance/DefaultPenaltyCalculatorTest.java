package com.photography.timekeeperbackend.domain.model.attendance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPenaltyCalculatorTest {

    private final PenaltyCalculator penaltyCalculator = new DefaultPenaltyCalculator();

    @DisplayName("출석은 패널티가 0원이다")
    @Test
    void presentPenaltyIsZero() {
        int penalty = penaltyCalculator.calculate(AttendanceStatus.PRESENT, 0);
        assertThat(penalty).isZero();
    }

    @DisplayName("결석은 패널티가 10000원이다")
    @Test
    void absentPenaltyIsFixed() {
        int penalty = penaltyCalculator.calculate(AttendanceStatus.ABSENT, 0);
        assertThat(penalty).isEqualTo(10000);
    }

    @DisplayName("지각 패널티는 분당 500원이며 10000원을 넘지 않는다")
    @Test
    void latePenaltyHasUpperBound() {
        int penalty = penaltyCalculator.calculate(AttendanceStatus.LATE, 40);
        assertThat(penalty).isEqualTo(10000);
    }

    @DisplayName("공결은 패널티가 0원이다")
    @Test
    void excusedPenaltyIsZero() {
        int penalty = penaltyCalculator.calculate(AttendanceStatus.EXCUSED, 100);
        assertThat(penalty).isZero();
    }
}
