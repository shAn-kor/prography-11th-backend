package com.photography.timekeeperbackend.domain.model.member.vo;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;

public final class LoginId {

    private static final int MAX_LENGTH = 50;

    private final String value;

    private LoginId(String value) {
        this.value = value;
    }

    public static LoginId of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "loginId는 비어 있을 수 없습니다.");
        }
        String normalized = raw.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "loginId 길이는 50자를 초과할 수 없습니다.");
        }
        return new LoginId(normalized);
    }

    public String value() {
        return value;
    }

    public boolean isSame(LoginId other) {
        return other != null && value.equals(other.value);
    }
}
