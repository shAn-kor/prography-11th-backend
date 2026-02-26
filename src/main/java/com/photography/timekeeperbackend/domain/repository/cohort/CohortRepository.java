package com.photography.timekeeperbackend.domain.repository.cohort;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;

import java.util.List;
import java.util.Optional;

public interface CohortRepository {
    long count();
    Cohort save(Cohort cohort);
    Optional<Cohort> findByCurrentTrue();
    Optional<Cohort> findByNumber(Integer number);
    Optional<Cohort> findById(Long id);
    List<Cohort> findAll();
}
