package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDepositHistoryRepository extends JpaRepository<DepositHistory, Long> {
    List<DepositHistory> findAllByCohortMemberIdOrderByCreatedAtDesc(Long cohortMemberId);
}
