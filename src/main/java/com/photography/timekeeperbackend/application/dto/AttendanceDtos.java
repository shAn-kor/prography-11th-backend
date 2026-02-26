package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.session.Session;

import java.util.List;

public final class AttendanceDtos {

    private AttendanceDtos() {
    }

    public record CheckCommand(String hashValue, Long memberId) {}
    public record RegisterCommand(Long sessionId, Long memberId, AttendanceStatus status) {}
    public record UpdateCommand(Long attendanceId, AttendanceStatus status) {}

    public record FindMyCommand(Long memberId) {}
    public record FindBySessionCommand(Long sessionId) {}
    public record FindByIdCommand(Long attendanceId) {}
    public record ValidateNotExistsCommand(Long sessionId, Long cohortMemberId) {}
    public record CreateByQrCommand(Session session, CohortMember cohortMember) {}
    public record CreateManualCommand(Session session, CohortMember cohortMember, AttendanceStatus status) {}
    public record PenaltyDiffCommand(Attendance attendance, CohortMember cohortMember, AttendanceStatus newStatus) {}
    public record DeleteCommand(Long attendanceId) {}

    public record Item(Attendance attendance) {}
    public record Items(List<Attendance> attendances) {}
    public record PenaltyDiff(int value) {}
}
