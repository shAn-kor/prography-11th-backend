package com.photography.timekeeperbackend.domain.model.cohort;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * - Part 엔티티: 기수 내 파트 종류를 관리한다.
 */
@Getter
@Entity
@Table(name = "part", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cohort_type", columnNames = {"cohort_id", "type"})
})
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartType type;

    protected Part() {
    }

    public static Part of(Cohort cohort, PartType type) {
        if (cohort == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "파트는 기수 정보가 필요합니다.");
        }
        if (type == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "파트 타입은 필수입니다.");
        }
        Part part = new Part();
        part.cohort = cohort;
        part.type = type;
        return part;
    }

}
