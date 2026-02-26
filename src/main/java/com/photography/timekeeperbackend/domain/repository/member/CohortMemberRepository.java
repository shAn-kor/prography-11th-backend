package com.photography.timekeeperbackend.domain.repository.member;

import com.photography.timekeeperbackend.domain.model.member.CohortMember;

import java.util.Optional;

public interface CohortMemberRepository {
    Optional<CohortMember> findById(Long id);
    Optional<CohortMember> findByMemberIdAndCohortId(Long memberId, Long cohortId);
    CohortMember save(CohortMember cohortMember);
    void deleteById(Long id);
}
