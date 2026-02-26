package com.photography.timekeeperbackend.domain.model.deposit;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepositHistoryTest {

    @DisplayName("보증금 이력 생성 시 입력값이 필드에 반영된다")
    @Test
    void ofCreatesHistoryWithGivenValues() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        DepositHistory history = DepositHistory.of(7L, DepositHistoryType.PENALTY, -3000, 97000, "지각");

        assertThat(history.getCohortMemberId()).isEqualTo(7L);
        assertThat(history.getType()).isEqualTo(DepositHistoryType.PENALTY);
        assertThat(history.getAmount()).isEqualTo(-3000);
        assertThat(history.getBalanceAfter()).isEqualTo(97000);
        assertThat(history.getDescription()).isEqualTo("지각");
        assertThat(history.getCreatedAt()).isAfter(before);
    }

    @DisplayName("잔액이 음수면 보증금 이력 생성 시 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void ofFailsWhenBalanceAfterNegative() {
        assertThatThrownBy(() -> DepositHistory.of(1L, DepositHistoryType.PENALTY, -1000, -1, "invalid"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
