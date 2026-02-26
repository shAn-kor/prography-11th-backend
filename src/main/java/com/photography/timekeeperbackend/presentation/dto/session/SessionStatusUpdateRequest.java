package com.photography.timekeeperbackend.presentation.dto.session;

import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import jakarta.validation.constraints.NotNull;

public record SessionStatusUpdateRequest(@NotNull SessionStatus status) {
}

