package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.repository.session.SessionRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaSessionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final JpaSessionRepository jpaRepository;

    @Override
    public Optional<Session> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public List<Session> findAll() { return jpaRepository.findAll(); }
    @Override
    public List<Session> findAllByStatusNot(SessionStatus status) { return jpaRepository.findAllByStatusNot(status); }
    @Override
    public Session save(Session session) { return jpaRepository.save(session); }
    @Override
    public void deleteById(Long id) { jpaRepository.deleteById(id); }
}
