package com.photography.timekeeperbackend.presentation.dto.attendance;

import com.photography.timekeeperbackend.domain.model.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceCreateRequest(
        @NotNull Long sessionId,
        @NotNull Long memberId,
        @NotNull AttendanceStatus status
) {
}

