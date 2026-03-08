package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaMemberRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository jpaRepository;

    @Override
    public Optional<Member> findByLoginId(String loginId) { return jpaRepository.findByLoginId(loginId); }
    @Override
    public boolean existsByLoginId(String loginId) { return jpaRepository.existsByLoginId(loginId); }
    @Override
    public Optional<Member> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public List<Member> findAll() { return jpaRepository.findAll(); }
    @Override
    public Member save(Member member) { return jpaRepository.saveAndFlush(member); }
    @Override
    public void deleteById(Long id) { jpaRepository.deleteById(id); }
}
