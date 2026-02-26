package com.photography.timekeeperbackend.presentation.dto.common;

import com.photography.timekeeperbackend.domain.exception.ErrorCode;

public record ErrorResponse(ErrorCode code, String message) {
}
