package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.DepositDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistoryType;
import com.photography.timekeeperbackend.domain.repository.deposit.DepositHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)



@RequiredArgsConstructor
public class DepositService {

    private final DepositHistoryRepository depositHistoryRepository;

    @Transactional
    public DepositDtos.Item initialize(DepositDtos.InitializeCommand command) {
        var cohortMember = command.cohortMember();
        DepositHistory history = DepositHistory.of(
                cohortMember.getId(),
                DepositHistoryType.INITIAL,
                100000,
                cohortMember.getDeposit(),
                "초기 보증금 설정"
        );
        return new DepositDtos.Item(depositHistoryRepository.save(history));
    }

    @Transactional
    public DepositDtos.Item deductPenalty(DepositDtos.DeductPenaltyCommand command) {
        command.cohortMember().deductDeposit(command.amount());
        DepositHistory history = DepositHistory.of(
                command.cohortMember().getId(),
                DepositHistoryType.PENALTY,
                -command.amount(),
                command.cohortMember().getDeposit(),
                command.description()
        );
        return new DepositDtos.Item(depositHistoryRepository.save(history));
    }

    @Transactional
    public DepositDtos.Item refund(DepositDtos.RefundCommand command) {
        command.cohortMember().refundDeposit(command.amount());
        DepositHistory history = DepositHistory.of(
                command.cohortMember().getId(),
                DepositHistoryType.REFUND,
                command.amount(),
                command.cohortMember().getDeposit(),
                command.description()
        );
        return new DepositDtos.Item(depositHistoryRepository.save(history));
    }

    public DepositDtos.Items getHistory(DepositDtos.GetHistoryCommand command) {
        return new DepositDtos.Items(depositHistoryRepository.findAllByCohortMemberIdOrderByCreatedAtDesc(command.cohortMemberId()));
    }
}
