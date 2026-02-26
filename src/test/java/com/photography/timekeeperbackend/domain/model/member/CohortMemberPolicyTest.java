package com.photography.timekeeperbackend.domain.model.member;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CohortMemberPolicyTest {

    @DisplayName("공결은 기수당 최대 3회까지만 가능하다")
    @Test
    void excuseIsLimitedByThreePerCohort() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 100000);

        cohortMember.incrementExcuseCount();
        cohortMember.incrementExcuseCount();
        cohortMember.incrementExcuseCount();

        assertThatThrownBy(cohortMember::incrementExcuseCount)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXCUSE_LIMIT_EXCEEDED);
    }

    @DisplayName("보증금보다 큰 금액을 차감하면 예외가 발생한다")
    @Test
    void cannotDeductMoreThanBalance() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 1000);

        assertThatThrownBy(() -> cohortMember.deductDeposit(5000))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DEPOSIT_INSUFFICIENT);
    }

    @DisplayName("보증금 차감 후 환급하면 잔액이 반영된다")
    @Test
    void deductAndRefundChangesBalance() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 100000);

        cohortMember.deductDeposit(3000);
        cohortMember.refundDeposit(1000);

        assertThat(cohortMember.getDeposit()).isEqualTo(98000);
    }

    @DisplayName("공결 횟수는 0 미만으로 내려가지 않는다")
    @Test
    void decrementExcuseCountDoesNotGoBelowZero() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 100000);

        cohortMember.decrementExcuseCount();

        assertThat(cohortMember.getExcuseCount()).isZero();
    }

    @DisplayName("0 이하 금액 차감은 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void deductDepositFailsWhenAmountNonPositive() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 100000);

        assertThatThrownBy(() -> cohortMember.deductDeposit(0))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("0 이하 금액 환급은 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void refundDepositFailsWhenAmountNonPositive() {
        CohortMember cohortMember = CohortMember.of(1L, 1L, 1L, 1L, 100000);

        assertThatThrownBy(() -> cohortMember.refundDeposit(0))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
