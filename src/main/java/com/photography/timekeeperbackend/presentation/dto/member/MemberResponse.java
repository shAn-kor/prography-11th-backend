package com.photography.timekeeperbackend.presentation.dto.member;

import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.member.MemberStatus;

public record MemberResponse(String loginId, String name, MemberStatus status) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getLoginId(), member.getName(), member.getStatus());
    }
}
