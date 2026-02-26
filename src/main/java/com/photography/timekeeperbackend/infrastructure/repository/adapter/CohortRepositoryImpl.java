package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.repository.cohort.CohortRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaCohortRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class CohortRepositoryImpl implements CohortRepository {

    private final JpaCohortRepository jpaRepository;

    @Override
    public long count() { return jpaRepository.count(); }
    @Override
    public Cohort save(Cohort cohort) { return jpaRepository.save(cohort); }
    @Override
    public Optional<Cohort> findByCurrentTrue() { return jpaRepository.findByCurrentTrue(); }
    @Override
    public Optional<Cohort> findByNumber(Integer number) { return jpaRepository.findByNumber(number); }
    @Override
    public Optional<Cohort> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public List<Cohort> findAll() { return jpaRepository.findAll(); }
}
