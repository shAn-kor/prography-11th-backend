package com.photography.timekeeperbackend.presentation.dto.deposit;

import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistoryType;

import java.time.LocalDateTime;

public record DepositHistoryResponse(DepositHistoryType type, int amount, int balanceAfter,
                                     String description, LocalDateTime createdAt) {

    public static DepositHistoryResponse from(DepositHistory history) {
        return new DepositHistoryResponse(
                history.getType(),
                history.getAmount(),
                history.getBalanceAfter(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }
}
