package com.photography.timekeeperbackend.domain.model.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CohortMemberDomainServiceTest {

    private final CohortMemberDomainService service = new CohortMemberDomainService();

    @DisplayName("EXCUSED 전이 시 공결 횟수를 증가시킨다")
    @Test
    void applyExcuseCountChangeIncrementsWhenMovedToExcused() {
        CohortMember cohortMember = mock(CohortMember.class);

        service.applyExcuseCountChange(cohortMember, false, true);

        verify(cohortMember).incrementExcuseCount();
        verify(cohortMember, never()).decrementExcuseCount();
    }

    @DisplayName("EXCUSED 이탈 시 공결 횟수를 감소시킨다")
    @Test
    void applyExcuseCountChangeDecrementsWhenMovedFromExcused() {
        CohortMember cohortMember = mock(CohortMember.class);

        service.applyExcuseCountChange(cohortMember, true, false);

        verify(cohortMember).decrementExcuseCount();
        verify(cohortMember, never()).incrementExcuseCount();
    }

    @DisplayName("생성 상태가 EXCUSED면 공결 횟수를 증가시킨다")
    @Test
    void applyExcuseCountOnCreateIncrementsForExcused() {
        CohortMember cohortMember = mock(CohortMember.class);

        service.applyExcuseCountOnCreate(cohortMember, true);

        verify(cohortMember).incrementExcuseCount();
    }

    @DisplayName("생성 상태가 EXCUSED가 아니면 공결 횟수를 건드리지 않는다")
    @Test
    void applyExcuseCountOnCreateDoesNothingForNonExcused() {
        CohortMember cohortMember = mock(CohortMember.class);

        service.applyExcuseCountOnCreate(cohortMember, false);

        verify(cohortMember, never()).incrementExcuseCount();
        verify(cohortMember, never()).decrementExcuseCount();
    }
}
