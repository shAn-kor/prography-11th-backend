package com.photography.timekeeperbackend.presentation.dto.session;

import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;

import java.time.LocalDateTime;

public record SessionResponse(String title, String description, SessionType type,
                              SessionStatus status, LocalDateTime sessionDate) {

    public static SessionResponse from(Session session) {
        return new SessionResponse(
                session.getTitle(),
                session.getDescription(),
                session.getType(),
                session.getStatus(),
                session.getSessionDate()
        );
    }
}
