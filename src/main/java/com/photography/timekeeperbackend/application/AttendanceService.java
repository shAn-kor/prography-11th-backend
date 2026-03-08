package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.AttendanceDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceDomainService;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.CohortMemberDomainService;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.repository.attendance.AttendanceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceDomainService attendanceDomainService;
    private final CohortMemberDomainService cohortMemberDomainService;

    public AttendanceDtos.Items findMyAttendances(AttendanceDtos.FindMyCommand command) {
        return new AttendanceDtos.Items(attendanceRepository.findAllByMemberId(command.memberId()));
    }

    public AttendanceDtos.Items findBySession(AttendanceDtos.FindBySessionCommand command) {
        return new AttendanceDtos.Items(attendanceRepository.findAllBySessionId(command.sessionId()));
    }

    public AttendanceDtos.Item findById(AttendanceDtos.FindByIdCommand command) {
        Attendance attendance = attendanceRepository.findById(command.attendanceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND, "출결을 찾을 수 없습니다."));
        return new AttendanceDtos.Item(attendance);
    }

    public void validateNotExists(AttendanceDtos.ValidateNotExistsCommand command) {
        if (attendanceRepository.findBySessionIdAndCohortMemberId(command.sessionId(), command.cohortMemberId()).isPresent()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_ALREADY_CHECKED, "이미 출석 체크가 완료되었습니다.");
        }
    }

    @Transactional
    public AttendanceDtos.Item createByQr(AttendanceDtos.CreateByQrCommand command) {
        Attendance attendance = attendanceDomainService.createByQr(
                command.session().getId(),
                command.cohortMember().getId(),
                command.session().getSessionDate()
        );
        try {
            return new AttendanceDtos.Item(attendanceRepository.save(attendance));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.ATTENDANCE_ALREADY_CHECKED, "이미 출석 체크가 완료되었습니다.");
        }
    }

    @Transactional
    public AttendanceDtos.Item createManual(AttendanceDtos.CreateManualCommand command) {
        cohortMemberDomainService.applyExcuseCountOnCreate(command.cohortMember(), command.status() == AttendanceStatus.EXCUSED);
        Attendance attendance = attendanceDomainService.createManual(
                command.session().getId(),
                command.cohortMember().getId(),
                command.status()
        );
        try {
            return new AttendanceDtos.Item(attendanceRepository.save(attendance));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.ATTENDANCE_ALREADY_CHECKED, "이미 출석 체크가 완료되었습니다.");
        }
    }

    @Transactional
    public AttendanceDtos.PenaltyDiff updateStatusAndGetPenaltyDiff(AttendanceDtos.PenaltyDiffCommand command) {
        cohortMemberDomainService.applyExcuseCountChange(
                command.cohortMember(),
                command.attendance().getStatus() == AttendanceStatus.EXCUSED,
                command.newStatus() == AttendanceStatus.EXCUSED
        );
        int diff = attendanceDomainService.updateStatusAndGetPenaltyDiff(command.attendance(), command.newStatus());
        return new AttendanceDtos.PenaltyDiff(diff);
    }

    @Transactional
    public void delete(AttendanceDtos.DeleteCommand command) {
        attendanceRepository.deleteById(command.attendanceId());
    }
}
