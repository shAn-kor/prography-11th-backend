package com.photography.timekeeperbackend.presentation.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceCheckRequest(
        @NotBlank String hashValue,
        @NotNull Long memberId
) {
}
