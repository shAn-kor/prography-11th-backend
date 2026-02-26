package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaPartRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class PartRepositoryImpl implements PartRepository {

    private final JpaPartRepository jpaRepository;

    @Override
    public Part save(Part part) { return jpaRepository.save(part); }
    @Override
    public Optional<Part> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public List<Part> findAll() { return jpaRepository.findAll(); }
}
