package com.photography.timekeeperbackend.domain.model.session;

/**
 * - SessionStatus 값객체: 일정 진행 생명주기 상태를 정의한다.
 */
public enum SessionStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
