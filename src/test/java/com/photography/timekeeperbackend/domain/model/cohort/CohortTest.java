package com.photography.timekeeperbackend.domain.model.cohort;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CohortTest {

    @DisplayName("기수 번호가 0 이하면 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void createFailsWhenNumberInvalid() {
        assertThatThrownBy(() -> Cohort.of(0, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("current=true이면 기수 번호는 11이어야 한다")
    @Test
    void createFailsWhenCurrentButNot11th() {
        assertThatThrownBy(() -> Cohort.of(10, true))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("11기는 current=true여야 한다")
    @Test
    void createFailsWhen11thButNotCurrent() {
        assertThatThrownBy(() -> Cohort.of(11, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("11기 current=true이면 운영 기수로 판정된다")
    @Test
    void isOperatingCohortTrueFor11th() {
        Cohort cohort = Cohort.of(11, true);

        assertThat(cohort.isOperatingCohort()).isTrue();
    }
}
