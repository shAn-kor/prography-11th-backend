package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaAttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findBySessionIdAndCohortMemberId(Long sessionId, Long cohortMemberId);

    @Query("""
            select a
            from Attendance a
            join CohortMember cm on cm.id = a.cohortMemberId
            where cm.memberId = :memberId
            """)
    List<Attendance> findAllByMemberId(@Param("memberId") Long memberId);

    List<Attendance> findAllBySessionId(Long sessionId);
}
