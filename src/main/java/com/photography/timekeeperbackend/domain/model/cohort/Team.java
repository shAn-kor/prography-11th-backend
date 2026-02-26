package com.photography.timekeeperbackend.domain.model.cohort;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * - Team 엔티티: 기수 내 팀 이름과 소속 기수 관계를 관리한다.
 */
@Getter
@Entity
@Table(name = "team", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cohort_name", columnNames = {"cohort_id", "name"})
})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Column(nullable = false, length = 50)
    private String name;

    protected Team() {
    }

    public static Team of(Cohort cohort, String name) {
        if (cohort == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "팀은 기수 정보가 필요합니다.");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "팀 이름은 비어 있을 수 없습니다.");
        }
        String normalized = name.trim();
        if (normalized.length() > 50) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "팀 이름 길이는 50자를 초과할 수 없습니다.");
        }
        Team team = new Team();
        team.cohort = cohort;
        team.name = normalized;
        return team;
    }

}
