package com.photography.timekeeperbackend.presentation.dto.session;

import com.photography.timekeeperbackend.domain.model.session.SessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SessionCreateRequest(
        @NotBlank String title,
        String description,
        @NotNull SessionType type,
        @NotNull LocalDateTime sessionDate
) {
}
