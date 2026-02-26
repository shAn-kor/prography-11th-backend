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
class MemberFacadeTest {

    @Autowired
    private MemberFacade memberFacade;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private PartRepository partRepository;

    @DisplayName("회원 등록 퍼사드는 회원/기수회원/초기 보증금 이력을 함께 생성한다")
    @Test
    void registerCreatesMemberAndDepositHistory() {
        Cohort cohort = cohortService.findCurrentCohort().cohort();
        Part part = partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(cohort.getId()))
                .findFirst()
                .orElseThrow();
        String loginId = "facade-member-" + System.nanoTime();

        Member member = memberFacade.register(new MemberDtos.RegisterCommand(loginId, "pw1234", "name", part.getId(), null)).member();

        CohortMember cohortMember = memberService.findCohortMember(
                new MemberDtos.FindCohortMemberCommand(member.getId(), cohort.getId())
        ).cohortMember();
        List<DepositHistory> histories = depositService.getHistory(new DepositDtos.GetHistoryCommand(cohortMember.getId())).histories();
        assertThat(member.getLoginId()).isEqualTo(loginId);
        assertThat(histories).isNotEmpty();
        assertThat(histories.get(0).getType()).isEqualTo(DepositHistoryType.INITIAL);
    }
}
