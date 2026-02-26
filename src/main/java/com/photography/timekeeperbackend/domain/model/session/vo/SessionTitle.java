package com.photography.timekeeperbackend.domain.model.session.vo;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;

public final class SessionTitle {

    private static final int MAX_LENGTH = 100;

    private final String value;

    private SessionTitle(String value) {
        this.value = value;
    }

    public static SessionTitle of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 제목은 비어 있을 수 없습니다.");
        }
        String normalized = raw.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 제목 길이는 100자를 초과할 수 없습니다.");
        }
        return new SessionTitle(normalized);
    }

    public String value() {
        return value;
    }

    public boolean isSame(SessionTitle other) {
        return other != null && value.equals(other.value);
    }
}
