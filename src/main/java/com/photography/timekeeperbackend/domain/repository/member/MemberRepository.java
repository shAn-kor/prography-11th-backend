package com.photography.timekeeperbackend.domain.repository.member;

import com.photography.timekeeperbackend.domain.model.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    Optional<Member> findById(Long id);
    List<Member> findAll();
    Member save(Member member);
    void deleteById(Long id);
}
