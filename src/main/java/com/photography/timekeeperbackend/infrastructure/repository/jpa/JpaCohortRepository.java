package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCohortRepository extends JpaRepository<Cohort, Long> {
    Optional<Cohort> findByCurrentTrue();
    Optional<Cohort> findByNumber(Integer number);
}
