package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;

import java.time.LocalDateTime;
import java.util.List;

public final class SessionDtos {

    private SessionDtos() {
    }

    public record CreateFacadeCommand(String title, String description, SessionType type, LocalDateTime sessionDate) {}
    public record CreateServiceCommand(Session session) {}
    public record FindByIdCommand(Long sessionId) {}
    public record UpdateCommand(Long sessionId, String title, String description, SessionType type, LocalDateTime sessionDate) {}
    public record DeleteCommand(Long sessionId) {}
    public record UpdateStatusCommand(Long sessionId, SessionStatus status) {}
    public record DeleteHardCommand(Long sessionId) {}

    public record Item(Session session) {}
    public record Items(List<Session> sessions) {}
}
