package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.presentation.dto.cohort.CohortResponse;
import com.photography.timekeeperbackend.application.CohortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController



@RequiredArgsConstructor
public class CohortController {

    private final CohortService cohortService;

    @GetMapping("/admin/cohorts")
    public ResponseEntity<List<CohortResponse>> getCohorts() {
        return ResponseEntity.ok(cohortService.findAll().cohorts().stream().map(CohortResponse::from).toList());
    }

    @GetMapping("/admin/cohorts/{id}")
    public ResponseEntity<CohortResponse> getCohort(@PathVariable Long id) {
        return ResponseEntity.ok(CohortResponse.from(cohortService.findById(new CohortDtos.FindByIdCommand(id)).cohort()));
    }
}
