package com.photography.timekeeperbackend.domain.model.member;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @DisplayName("회원 생성 시 상태는 ACTIVE다")
    @Test
    void createSetsActiveStatus() {
        Member member = Member.create("user1", "encoded", "name", MemberRole.USER);

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @DisplayName("withdraw 호출 시 상태가 WITHDRAWN으로 변경된다")
    @Test
    void withdrawChangesStatus() {
        Member member = Member.create("user1", "encoded", "name", MemberRole.USER);

        member.withdraw();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }

    @DisplayName("탈퇴한 회원은 출석 가능 검증에서 예외가 발생한다")
    @Test
    void validateCanAttendThrowsWhenWithdrawn() {
        Member member = Member.create("user1", "encoded", "name", MemberRole.USER);
        member.withdraw();

        assertThatThrownBy(member::validateCanAttend)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_WITHDRAWN);
    }

    @DisplayName("이름 변경 시 name 필드가 갱신된다")
    @Test
    void updateNameChangesValue() {
        Member member = Member.create("user1", "encoded", "before", MemberRole.USER);

        member.updateName("after");

        assertThat(member.getName()).isEqualTo("after");
    }

    @DisplayName("빈 loginId로 회원 생성 시 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void createFailsWhenLoginIdBlank() {
        assertThatThrownBy(() -> Member.create(" ", "encoded", "name", MemberRole.USER))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("빈 이름으로 회원 이름 변경 시 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void updateNameFailsWhenBlank() {
        Member member = Member.create("user1", "encoded", "before", MemberRole.USER);

        assertThatThrownBy(() -> member.updateName(" "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
