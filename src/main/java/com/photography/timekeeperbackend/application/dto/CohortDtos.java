package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.Team;

import java.util.List;

public final class CohortDtos {

    private CohortDtos() {
    }

    public record FindByIdCommand(Long id) {}
    public record FindPartByIdCommand(Long partId) {}
    public record FindTeamByIdNullableCommand(Long teamId) {}

    public record Item(Cohort cohort) {}
    public record Items(List<Cohort> cohorts) {}
    public record PartItem(Part part) {}
    public record TeamItem(Team team) {}
}
