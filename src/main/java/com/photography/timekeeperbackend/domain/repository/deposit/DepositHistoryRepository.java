package com.photography.timekeeperbackend.domain.repository.deposit;

import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;

import java.util.List;

public interface DepositHistoryRepository {
    DepositHistory save(DepositHistory history);
    List<DepositHistory> findAllByCohortMemberIdOrderByCreatedAtDesc(Long cohortMemberId);
}
