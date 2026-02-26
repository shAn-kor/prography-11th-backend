package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaSessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByStatusNot(SessionStatus status);
}
