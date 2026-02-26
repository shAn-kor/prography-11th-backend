package com.photography.timekeeperbackend.domain.repository.cohort;

import com.photography.timekeeperbackend.domain.model.cohort.Team;

import java.util.Optional;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(Long id);
}
