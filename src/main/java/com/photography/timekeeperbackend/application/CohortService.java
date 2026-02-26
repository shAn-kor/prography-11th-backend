package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.Team;
import com.photography.timekeeperbackend.domain.repository.cohort.CohortRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.TeamRepository;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)



@RequiredArgsConstructor
public class CohortService {

    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;

    public CohortDtos.Items findAll() {
        return new CohortDtos.Items(cohortRepository.findAll());
    }

    public CohortDtos.Item findById(CohortDtos.FindByIdCommand command) {
        Cohort cohort = cohortRepository.findById(command.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_NOT_FOUND, "기수를 찾을 수 없습니다."));
        return new CohortDtos.Item(cohort);
    }

    public CohortDtos.Item findCurrentCohort() {
        Cohort cohort = cohortRepository.findByNumber(11)
                .orElseGet(() -> cohortRepository.findByCurrentTrue()
                        .orElseThrow(() -> new BusinessException(ErrorCode.COHORT_NOT_FOUND, "현재 기수를 찾을 수 없습니다.")));
        return new CohortDtos.Item(cohort);
    }

    public CohortDtos.PartItem findPartById(CohortDtos.FindPartByIdCommand command) {
        Part part = partRepository.findById(command.partId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PART_NOT_FOUND, "파트를 찾을 수 없습니다."));
        return new CohortDtos.PartItem(part);
    }

    public CohortDtos.TeamItem findTeamByIdNullable(CohortDtos.FindTeamByIdNullableCommand command) {
        if (command.teamId() == null) {
            return new CohortDtos.TeamItem(null);
        }
        Team team = teamRepository.findById(command.teamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND, "팀을 찾을 수 없습니다."));
        return new CohortDtos.TeamItem(team);
    }
}
