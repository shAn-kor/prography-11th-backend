package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaCohortMemberRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class CohortMemberRepositoryImpl implements CohortMemberRepository {

    private final JpaCohortMemberRepository jpaRepository;

    @Override
    public Optional<CohortMember> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<CohortMember> findByMemberIdAndCohortId(Long memberId, Long cohortId) {
        return jpaRepository.findByMemberIdAndCohortId(memberId, cohortId);
    }

    @Override
    public CohortMember save(CohortMember cohortMember) { return jpaRepository.save(cohortMember); }

    @Override
    public void deleteById(Long id) { jpaRepository.deleteById(id); }
}
