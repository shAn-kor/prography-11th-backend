package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.cohort.Team;
import com.photography.timekeeperbackend.domain.repository.cohort.TeamRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaTeamRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository {

    private final JpaTeamRepository jpaRepository;

    @Override
    public Team save(Team team) { return jpaRepository.save(team); }
    @Override
    public Optional<Team> findById(Long id) { return jpaRepository.findById(id); }
}
