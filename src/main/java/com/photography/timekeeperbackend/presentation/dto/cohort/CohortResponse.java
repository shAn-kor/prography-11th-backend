package com.photography.timekeeperbackend.presentation.dto.cohort;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;

public record CohortResponse(Integer number, boolean current) {
    public static CohortResponse from(Cohort cohort) {
        return new CohortResponse(cohort.getNumber(), cohort.getCurrent());
    }
}
