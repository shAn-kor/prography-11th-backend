package com.photography.timekeeperbackend.presentation.dto.member;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(@NotBlank String name) {
}
