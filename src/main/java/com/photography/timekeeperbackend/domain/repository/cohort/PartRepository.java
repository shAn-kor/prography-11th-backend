package com.photography.timekeeperbackend.domain.repository.cohort;

import com.photography.timekeeperbackend.domain.model.cohort.Part;

import java.util.List;
import java.util.Optional;

public interface PartRepository {
    Part save(Part part);
    Optional<Part> findById(Long id);
    List<Part> findAll();
}
