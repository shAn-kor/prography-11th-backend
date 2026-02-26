package com.photography.timekeeperbackend.domain.model.member;

import com.photography.timekeeperbackend.domain.model.common.BaseTimeEntity;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.member.vo.LoginId;
import com.photography.timekeeperbackend.domain.model.member.vo.MemberName;
import jakarta.persistence.*;

/**
 * - Member 엔티티: 로그인 계정 정보와 회원 상태를 관리한다.
 */
@Entity
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    protected Member() {
    }

    public static Member create(String loginId, String encodedPassword, String name, MemberRole role) {
        Member member = new Member();
        member.loginId = LoginId.of(loginId).value();
        member.password = validateEncodedPassword(encodedPassword);
        member.name = MemberName.of(name).value();
        member.role = validateRole(role);
        member.status = MemberStatus.ACTIVE;
        return member;
    }

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public MemberRole getRole() { return role; }
    public MemberStatus getStatus() { return status; }

    public boolean isWithdrawn() { return status == MemberStatus.WITHDRAWN; }

    public void updateName(String name) {
        this.name = MemberName.of(name).value();
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    public void validateCanAttend() {
        if (isWithdrawn()) {
            throw new BusinessException(ErrorCode.MEMBER_WITHDRAWN, "탈퇴한 회원은 출석할 수 없습니다.");
        }
    }

    private static String validateEncodedPassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "비밀번호는 비어 있을 수 없습니다.");
        }
        if (encodedPassword.length() > 255) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "비밀번호 길이는 255자를 초과할 수 없습니다.");
        }
        return encodedPassword;
    }

    private static MemberRole validateRole(MemberRole role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "회원 역할은 필수입니다.");
        }
        return role;
    }
}
