package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CohortServiceTest {

    @Autowired
    private CohortService cohortService;

    @DisplayName("현재 기수 조회 시 11기를 반환한다")
    @Test
    void findCurrentCohortReturns11th() {
        Cohort cohort = cohortService.findCurrentCohort().cohort();

        assertThat(cohort.getNumber()).isEqualTo(11);
    }

    @DisplayName("존재하지 않는 파트 조회 시 PART_NOT_FOUND 예외를 던진다")
    @Test
    void throwsWhenPartNotFound() {
        assertThatThrownBy(() -> cohortService.findPartById(new CohortDtos.FindPartByIdCommand(Long.MAX_VALUE)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PART_NOT_FOUND);
    }

    @DisplayName("teamId가 null이면 null을 반환한다")
    @Test
    void returnsNullWhenTeamIdIsNull() {
        Team result = cohortService.findTeamByIdNullable(new CohortDtos.FindTeamByIdNullableCommand(null)).team();

        assertThat(result).isNull();
    }
}
