package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface JpaSessionRepository extends JpaRepository<Session, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Session s where s.id = :id")
    Optional<Session> findByIdForUpdate(@Param("id") Long id);

    List<Session> findAllByStatusNot(SessionStatus status);
}
