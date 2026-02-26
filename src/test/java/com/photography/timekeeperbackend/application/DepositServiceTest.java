package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import com.photography.timekeeperbackend.application.dto.DepositDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistoryType;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DepositServiceTest {

    @Autowired
    private DepositService depositService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private PartRepository partRepository;

    @DisplayName("초기 보증금 이력은 INITIAL로 저장한다")
    @Test
    void initializeCreatesInitialHistory() {
        CohortMember cohortMember = createCohortMember();

        DepositHistory history = depositService.initialize(new DepositDtos.InitializeCommand(cohortMember)).history();

        assertThat(history.getType()).isEqualTo(DepositHistoryType.INITIAL);
        assertThat(history.getAmount()).isEqualTo(100000);
    }

    @DisplayName("패널티 차감 시 PENALTY 이력은 음수 금액으로 저장한다")
    @Test
    void deductPenaltyStoresNegativeAmount() {
        CohortMember cohortMember = createCohortMember();

        DepositHistory history = depositService.deductPenalty(
                new DepositDtos.DeductPenaltyCommand(cohortMember, 5000, "패널티")
        ).history();

        assertThat(history.getType()).isEqualTo(DepositHistoryType.PENALTY);
        assertThat(history.getAmount()).isEqualTo(-5000);
        assertThat(history.getBalanceAfter()).isEqualTo(95000);
    }

    @DisplayName("이력 조회는 생성된 이력을 반환한다")
    @Test
    void getHistoryReturnsStoredHistories() {
        CohortMember cohortMember = createCohortMember();
        depositService.initialize(new DepositDtos.InitializeCommand(cohortMember));

        List<DepositHistory> histories = depositService.getHistory(new DepositDtos.GetHistoryCommand(cohortMember.getId())).histories();

        assertThat(histories).isNotEmpty();
        assertThat(histories.get(0).getCohortMemberId()).isEqualTo(cohortMember.getId());
    }

    private CohortMember createCohortMember() {
        String loginId = "deposit-" + System.nanoTime();
        Member member = memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name")).member();
        Cohort cohort = cohortService.findCurrentCohort().cohort();
        Part part = partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(cohort.getId()))
                .findFirst()
                .orElseThrow();
        return memberService.saveCohortMember(new MemberDtos.SaveCohortMemberCommand(
                CohortMember.create(member.getId(), cohort.getId(), part.getId(), null, 100000)
        )).cohortMember();
    }
}
