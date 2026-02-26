package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCohortMemberRepository extends JpaRepository<CohortMember, Long> {
    Optional<CohortMember> findByMemberIdAndCohortId(Long memberId, Long cohortId);
}
