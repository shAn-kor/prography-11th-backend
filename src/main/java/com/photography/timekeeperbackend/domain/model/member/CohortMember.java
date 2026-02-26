package com.photography.timekeeperbackend.domain.model.member;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;

/**
 * - CohortMember 엔티티: 회원의 기수 소속과 보증금/공결 횟수를 관리한다.
 */
@Entity
@Table(name = "cohort_member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_cohort", columnNames = {"member_id", "cohort_id"})
})
public class CohortMember {

    private static final int MAX_EXCUSE_COUNT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "cohort_id", nullable = false)
    private Long cohortId;

    @Column(name = "part_id", nullable = false)
    private Long partId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(nullable = false)
    private int deposit;

    @Column(nullable = false)
    private int excuseCount;

    protected CohortMember() {
    }

    public static CohortMember create(Long memberId, Long cohortId, Long partId, Long teamId, int deposit) {
        validateIdentity(memberId, "memberId");
        validateIdentity(cohortId, "cohortId");
        validateIdentity(partId, "partId");
        if (teamId != null) {
            validateIdentity(teamId, "teamId");
        }
        validateDeposit(deposit);
        CohortMember cohortMember = new CohortMember();
        cohortMember.memberId = memberId;
        cohortMember.cohortId = cohortId;
        cohortMember.partId = partId;
        cohortMember.teamId = teamId;
        cohortMember.deposit = deposit;
        cohortMember.excuseCount = 0;
        return cohortMember;
    }

    // for unit policy tests without JPA graph
    public static CohortMember of(Long memberId, Long cohortId, Long partId, Long teamId, int deposit) {
        validateIdentity(memberId, "memberId");
        validateIdentity(cohortId, "cohortId");
        validateIdentity(partId, "partId");
        if (teamId != null) {
            validateIdentity(teamId, "teamId");
        }
        validateDeposit(deposit);
        CohortMember cohortMember = new CohortMember();
        cohortMember.memberId = memberId;
        cohortMember.cohortId = cohortId;
        cohortMember.partId = partId;
        cohortMember.teamId = teamId;
        cohortMember.deposit = deposit;
        cohortMember.excuseCount = 0;
        return cohortMember;
    }

    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public Long getCohortId() { return cohortId; }
    public Long getPartId() { return partId; }
    public Long getTeamId() { return teamId; }
    public int getDeposit() { return deposit; }
    public int getExcuseCount() { return excuseCount; }

    public void deductDeposit(int amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "차감 금액은 1 이상이어야 합니다.");
        }
        if (amount > deposit) {
            throw new BusinessException(ErrorCode.DEPOSIT_INSUFFICIENT, "보증금 잔액이 부족합니다.");
        }
        deposit -= amount;
    }

    public void refundDeposit(int amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "환급 금액은 1 이상이어야 합니다.");
        }
        deposit += amount;
    }

    public void incrementExcuseCount() {
        if (excuseCount >= MAX_EXCUSE_COUNT) {
            throw new BusinessException(ErrorCode.EXCUSE_LIMIT_EXCEEDED, "공결은 기수당 최대 3회까지 허용됩니다.");
        }
        excuseCount++;
    }

    public void decrementExcuseCount() {
        if (excuseCount > 0) {
            excuseCount--;
        }
    }

    private static void validateIdentity(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, fieldName + "는 1 이상이어야 합니다.");
        }
    }

    private static void validateDeposit(int deposit) {
        if (deposit < 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "보증금은 0 이상이어야 합니다.");
        }
    }
}
