package com.photography.timekeeperbackend.domain.model.member;

/**
 * - CohortMember 도메인 서비스: 출결 상태 전이에 따른 공결 횟수 변경 규칙을 적용한다.
 */
public class CohortMemberDomainService {

    public void applyExcuseCountChange(CohortMember cohortMember, boolean oldExcused, boolean newExcused) {
        if (!oldExcused && newExcused) {
            cohortMember.incrementExcuseCount();
        } else if (oldExcused && !newExcused) {
            cohortMember.decrementExcuseCount();
        }
    }

    public void applyExcuseCountOnCreate(CohortMember cohortMember, boolean excused) {
        if (excused) {
            cohortMember.incrementExcuseCount();
        }
    }
}
