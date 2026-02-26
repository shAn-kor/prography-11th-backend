package com.photography.timekeeperbackend.domain.model.cohort;

import com.photography.timekeeperbackend.domain.model.common.BaseTimeEntity;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * - Cohort 엔티티: 운영 기수 번호와 현재 운영 여부를 표현한다.
 */
@Entity
@Table(name = "cohort")
@Getter
public class Cohort extends BaseTimeEntity {

    private static final int OPERATING_COHORT_NUMBER = 11;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Boolean current;

    protected Cohort() {
    }

    public static Cohort of(Integer number, boolean current) {
        if (number == null || number <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "기수 번호는 1 이상이어야 합니다.");
        }
        if (current && number != OPERATING_COHORT_NUMBER) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "현재 운영 기수는 11기로 고정입니다.");
        }
        if (!current && number == OPERATING_COHORT_NUMBER) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "11기는 current=true여야 합니다.");
        }
        Cohort cohort = new Cohort();
        cohort.number = number;
        cohort.current = current;
        return cohort;
    }

    public boolean isOperatingCohort() {
        return number != null && number == OPERATING_COHORT_NUMBER;
    }
}
