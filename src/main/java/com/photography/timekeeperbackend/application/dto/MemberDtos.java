package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;

import java.util.List;

public final class MemberDtos {

    private MemberDtos() {
    }

    public record RegisterCommand(String loginId, String password, String name, Long partId, Long teamId) {}
    public record LoginCommand(String loginId, String rawPassword) {}
    public record FindByIdCommand(Long memberId) {}
    public record CreateCommand(String loginId, String rawPassword, String name) {}
    public record SaveCohortMemberCommand(CohortMember cohortMember) {}
    public record DeleteCohortMemberCommand(Long cohortMemberId) {}
    public record DeleteMemberCommand(Long memberId) {}
    public record UpdateCommand(Long memberId, String name) {}
    public record WithdrawCommand(Long memberId) {}
    public record FindCohortMemberCommand(Long memberId, Long cohortId) {}
    public record FindCohortMemberByIdCommand(Long cohortMemberId) {}
    public record FindMemberByCohortMemberIdCommand(Long cohortMemberId) {}

    public record Item(Member member) {}
    public record Items(List<Member> members) {}
    public record CohortMemberItem(CohortMember cohortMember) {}
}
