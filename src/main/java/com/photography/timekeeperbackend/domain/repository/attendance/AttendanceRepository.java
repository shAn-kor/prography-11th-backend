package com.photography.timekeeperbackend.domain.repository.attendance;

import com.photography.timekeeperbackend.domain.model.attendance.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {
    Optional<Attendance> findById(Long id);
    Optional<Attendance> findBySessionIdAndCohortMemberId(Long sessionId, Long cohortMemberId);
    List<Attendance> findAllByMemberId(Long memberId);
    List<Attendance> findAllBySessionId(Long sessionId);
    Attendance save(Attendance attendance);
    void deleteById(Long id);
}
