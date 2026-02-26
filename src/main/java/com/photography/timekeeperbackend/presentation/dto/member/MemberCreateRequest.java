package com.photography.timekeeperbackend.presentation.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberCreateRequest(
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull Long partId,
        Long teamId
) {
}
