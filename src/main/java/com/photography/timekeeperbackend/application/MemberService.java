package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.MemberDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.member.MemberRole;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDtos.Item login(MemberDtos.LoginCommand command) {
        Member member = memberRepository.findByLoginId(command.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));

        if (member.isWithdrawn()) {
            throw new BusinessException(ErrorCode.MEMBER_WITHDRAWN, "탈퇴한 회원은 로그인할 수 없습니다.");
        }

        if (!passwordEncoder.matches(command.rawPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED, "비밀번호가 일치하지 않습니다.");
        }

        return new MemberDtos.Item(member);
    }

    public MemberDtos.Item findById(MemberDtos.FindByIdCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));
        return new MemberDtos.Item(member);
    }

    public MemberDtos.Items findAll() {
        return new MemberDtos.Items(memberRepository.findAll());
    }

    @Transactional
    public MemberDtos.Item createMember(MemberDtos.CreateCommand command) {
        if (memberRepository.existsByLoginId(command.loginId())) {
            throw new BusinessException(ErrorCode.LOGIN_ID_DUPLICATED, "이미 사용 중인 loginId 입니다.");
        }

        Member member = Member.create(command.loginId(), passwordEncoder.encode(command.rawPassword()), command.name(), MemberRole.USER);
        return new MemberDtos.Item(memberRepository.save(member));
    }

    @Transactional
    public MemberDtos.CohortMemberItem saveCohortMember(MemberDtos.SaveCohortMemberCommand command) {
        return new MemberDtos.CohortMemberItem(cohortMemberRepository.save(command.cohortMember()));
    }

    @Transactional
    public void deleteCohortMember(MemberDtos.DeleteCohortMemberCommand command) {
        cohortMemberRepository.deleteById(command.cohortMemberId());
    }

    @Transactional
    public void deleteMember(MemberDtos.DeleteMemberCommand command) {
        memberRepository.deleteById(command.memberId());
    }

    @Transactional
    public MemberDtos.Item updateMember(MemberDtos.UpdateCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));
        member.updateName(command.name());
        return new MemberDtos.Item(member);
    }

    @Transactional
    public void withdrawMember(MemberDtos.WithdrawCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));
        member.withdraw();
    }

    public MemberDtos.CohortMemberItem findCohortMember(MemberDtos.FindCohortMemberCommand command) {
        CohortMember cohortMember = cohortMemberRepository.findByMemberIdAndCohortId(command.memberId(), command.cohortId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_MEMBER_NOT_FOUND, "기수 회원 정보를 찾을 수 없습니다."));
        return new MemberDtos.CohortMemberItem(cohortMember);
    }

    public MemberDtos.CohortMemberItem findCohortMemberById(MemberDtos.FindCohortMemberByIdCommand command) {
        CohortMember cohortMember = cohortMemberRepository.findById(command.cohortMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_MEMBER_NOT_FOUND, "기수 회원 정보를 찾을 수 없습니다."));
        return new MemberDtos.CohortMemberItem(cohortMember);
    }

    public MemberDtos.Item findMemberByCohortMemberId(MemberDtos.FindMemberByCohortMemberIdCommand command) {
        CohortMember cohortMember = cohortMemberRepository.findById(command.cohortMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_MEMBER_NOT_FOUND, "기수 회원 정보를 찾을 수 없습니다."));
        Member member = memberRepository.findById(cohortMember.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원을 찾을 수 없습니다."));
        return new MemberDtos.Item(member);
    }
}
