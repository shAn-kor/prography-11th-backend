package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import com.photography.timekeeperbackend.application.dto.DepositDtos;
import com.photography.timekeeperbackend.application.dto.MemberDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.Team;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class MemberFacade {

    private static final int INITIAL_DEPOSIT = 100000;

    private final MemberService memberService;
    private final CohortService cohortService;
    private final DepositService depositService;

    public MemberDtos.Item register(MemberDtos.RegisterCommand command) {
        Member member = memberService.createMember(new MemberDtos.CreateCommand(command.loginId(), command.password(), command.name())).member();
        CohortMember savedCohortMember = null;
        try {
            Cohort currentCohort = cohortService.findCurrentCohort().cohort();
            Part part = cohortService.findPartById(new CohortDtos.FindPartByIdCommand(command.partId())).part();
            Team team = cohortService.findTeamByIdNullable(new CohortDtos.FindTeamByIdNullableCommand(command.teamId())).team();
            CohortMember cohortMember = CohortMember.create(
                    member.getId(),
                    currentCohort.getId(),
                    part.getId(),
                    team == null ? null : team.getId(),
                    INITIAL_DEPOSIT
            );
            savedCohortMember = memberService.saveCohortMember(new MemberDtos.SaveCohortMemberCommand(cohortMember)).cohortMember();
            depositService.initialize(new DepositDtos.InitializeCommand(savedCohortMember));
            return new MemberDtos.Item(member);
        } catch (RuntimeException ex) {
            if (savedCohortMember != null && savedCohortMember.getId() != null) {
                memberService.deleteCohortMember(new MemberDtos.DeleteCohortMemberCommand(savedCohortMember.getId()));
            }
            memberService.deleteMember(new MemberDtos.DeleteMemberCommand(member.getId()));
            throw ex;
        }
    }
}
