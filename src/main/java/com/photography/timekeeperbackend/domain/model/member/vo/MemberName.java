package com.photography.timekeeperbackend.domain.model.member.vo;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;

public final class MemberName {

    private static final int MAX_LENGTH = 50;

    private final String value;

    private MemberName(String value) {
        this.value = value;
    }

    public static MemberName of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "회원 이름은 비어 있을 수 없습니다.");
        }
        String normalized = raw.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "회원 이름 길이는 50자를 초과할 수 없습니다.");
        }
        return new MemberName(normalized);
    }

    public String value() {
        return value;
    }

    public boolean contains(String keyword) {
        return keyword != null && !keyword.isBlank() && value.contains(keyword.trim());
    }
}
