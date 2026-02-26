package com.photography.timekeeperbackend.domain.model.deposit;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * - DepositHistory 엔티티: 보증금 변동 내역과 잔액 스냅샷을 저장한다.
 */
@Entity
@Table(name = "deposit_history")
public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cohort_member_id", nullable = false)
    private Long cohortMemberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositHistoryType type;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int balanceAfter;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected DepositHistory() {
    }

    public static DepositHistory of(Long cohortMemberId, DepositHistoryType type, int amount,
                                    int balanceAfter, String description) {
        if (cohortMemberId == null || cohortMemberId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "cohortMemberId는 1 이상이어야 합니다.");
        }
        if (type == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "보증금 이력 타입은 필수입니다.");
        }
        if (balanceAfter < 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "보증금 잔액은 0 이상이어야 합니다.");
        }
        String normalizedDescription = description == null ? null : description.trim();
        if (normalizedDescription != null && normalizedDescription.length() > 255) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "설명 길이는 255자를 초과할 수 없습니다.");
        }

        DepositHistory history = new DepositHistory();
        history.cohortMemberId = cohortMemberId;
        history.type = type;
        history.amount = amount;
        history.balanceAfter = balanceAfter;
        history.description = normalizedDescription;
        history.createdAt = LocalDateTime.now();
        return history;
    }

    public Long getId() { return id; }
    public Long getCohortMemberId() { return cohortMemberId; }
    public DepositHistoryType getType() { return type; }
    public int getAmount() { return amount; }
    public int getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
