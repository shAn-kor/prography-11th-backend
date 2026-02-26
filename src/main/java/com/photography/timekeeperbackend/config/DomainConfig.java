package com.photography.timekeeperbackend.config;

import com.photography.timekeeperbackend.domain.model.attendance.AttendanceDomainService;
import com.photography.timekeeperbackend.domain.model.attendance.PenaltyCalculator;
import com.photography.timekeeperbackend.domain.model.member.CohortMemberDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public AttendanceDomainService attendanceDomainService(PenaltyCalculator penaltyCalculator) {
        return new AttendanceDomainService(penaltyCalculator);
    }

    @Bean
    public CohortMemberDomainService cohortMemberDomainService() {
        return new CohortMemberDomainService();
    }
}
