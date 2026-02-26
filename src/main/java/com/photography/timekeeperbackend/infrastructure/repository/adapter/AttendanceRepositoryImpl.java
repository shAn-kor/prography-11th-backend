package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.repository.attendance.AttendanceRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaAttendanceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final JpaAttendanceRepository jpaRepository;

    @Override
    public Optional<Attendance> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public Optional<Attendance> findBySessionIdAndCohortMemberId(Long sessionId, Long cohortMemberId) {
        return jpaRepository.findBySessionIdAndCohortMemberId(sessionId, cohortMemberId);
    }
    @Override
    public List<Attendance> findAllByMemberId(Long memberId) { return jpaRepository.findAllByMemberId(memberId); }
    @Override
    public List<Attendance> findAllBySessionId(Long sessionId) { return jpaRepository.findAllBySessionId(sessionId); }
    @Override
    public Attendance save(Attendance attendance) { return jpaRepository.save(attendance); }
    @Override
    public void deleteById(Long id) { jpaRepository.deleteById(id); }
}
