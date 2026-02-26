package com.photography.timekeeperbackend.domain.repository.session;

import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Optional<Session> findById(Long id);
    List<Session> findAll();
    List<Session> findAllByStatusNot(SessionStatus status);
    Session save(Session session);
    void deleteById(Long id);
}
