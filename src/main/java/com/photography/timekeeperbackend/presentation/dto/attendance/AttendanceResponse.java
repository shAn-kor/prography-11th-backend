package com.photography.timekeeperbackend.presentation.dto.attendance;

import com.photography.timekeeperbackend.domain.model.attendance.Attendance;
import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;

import java.time.LocalDateTime;

public record AttendanceResponse(String memberLoginId, String sessionTitle, LocalDateTime sessionDate,
                                 AttendanceStatus status, int lateMinutes, int penaltyAmount) {

    public static AttendanceResponse from(Attendance attendance, String memberLoginId, String sessionTitle, LocalDateTime sessionDate) {
        return new AttendanceResponse(
                memberLoginId,
                sessionTitle,
                sessionDate,
                attendance.getStatus(),
                attendance.getLateMinutes(),
                attendance.getPenaltyAmount()
        );
    }
}
