package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;

import java.util.List;

public final class DepositDtos {

    private DepositDtos() {
    }

    public record InitializeCommand(CohortMember cohortMember) {}
    public record DeductPenaltyCommand(CohortMember cohortMember, int amount, String description) {}
    public record RefundCommand(CohortMember cohortMember, int amount, String description) {}
    public record GetHistoryCommand(Long cohortMemberId) {}

    public record Item(DepositHistory history) {}
    public record Items(List<DepositHistory> histories) {}
}
