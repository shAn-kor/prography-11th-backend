package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.MemberDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("회원 등록 후 같은 계정으로 로그인할 수 있다")
    @Test
    void loginSuccess() {
        String loginId = "user-" + System.nanoTime();
        memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name"));

        Member result = memberService.login(new MemberDtos.LoginCommand(loginId, "pw1234")).member();

        assertThat(result.getLoginId()).isEqualTo(loginId);
    }

    @DisplayName("회원 등록 시 중복 loginId는 충돌 예외를 던진다")
    @Test
    void createMemberRejectsDuplicateLoginId() {
        assertThatThrownBy(() -> memberService.createMember(new MemberDtos.CreateCommand("admin", "pw", "name")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.LOGIN_ID_DUPLICATED);
    }

    @DisplayName("탈퇴 회원은 로그인할 수 없다")
    @Test
    void withdrawnMemberCannotLogin() {
        String loginId = "withdraw-" + System.nanoTime();
        Member member = memberService.createMember(new MemberDtos.CreateCommand(loginId, "pw1234", "name")).member();
        memberService.withdrawMember(new MemberDtos.WithdrawCommand(member.getId()));

        assertThatThrownBy(() -> memberService.login(new MemberDtos.LoginCommand(loginId, "pw1234")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_WITHDRAWN);
    }
}
