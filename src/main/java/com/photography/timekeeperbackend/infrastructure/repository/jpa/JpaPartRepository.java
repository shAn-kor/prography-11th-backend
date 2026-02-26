package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.cohort.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPartRepository extends JpaRepository<Part, Long> {
}
