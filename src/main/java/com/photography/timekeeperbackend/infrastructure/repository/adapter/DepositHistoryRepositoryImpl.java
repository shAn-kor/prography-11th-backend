package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.repository.deposit.DepositHistoryRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaDepositHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository



@RequiredArgsConstructor
public class DepositHistoryRepositoryImpl implements DepositHistoryRepository {

    private final JpaDepositHistoryRepository jpaRepository;

    @Override
    public DepositHistory save(DepositHistory history) { return jpaRepository.save(history); }

    @Override
    public List<DepositHistory> findAllByCohortMemberIdOrderByCreatedAtDesc(Long cohortMemberId) {
        return jpaRepository.findAllByCohortMemberIdOrderByCreatedAtDesc(cohortMemberId);
    }
}
