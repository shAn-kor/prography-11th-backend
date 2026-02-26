package com.photography.timekeeperbackend.domain.model.session;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.common.BaseTimeEntity;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.session.vo.SessionTitle;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * - Session 엔티티: 기수 모임 일정의 내용, 유형, 진행 상태를 관리한다.
 */
@Entity
@Table(name = "session")
public class Session extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id", nullable = false)
    private Cohort cohort;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(nullable = false)
    private LocalDateTime sessionDate;

    protected Session() {
    }

    public static Session create(Cohort cohort, String title, String description, SessionType type, LocalDateTime sessionDate) {
        Session session = new Session();
        session.cohort = validateCohort(cohort);
        session.title = SessionTitle.of(title).value();
        session.description = validateDescription(description);
        session.type = validateType(type);
        session.status = SessionStatus.SCHEDULED;
        session.sessionDate = validateSessionDate(sessionDate);
        return session;
    }

    public Long getId() { return id; }
    public Cohort getCohort() { return cohort; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public SessionType getType() { return type; }
    public SessionStatus getStatus() { return status; }
    public LocalDateTime getSessionDate() { return sessionDate; }

    public void update(String title, String description, SessionType type, LocalDateTime sessionDate) {
        this.title = SessionTitle.of(title).value();
        this.description = validateDescription(description);
        this.type = validateType(type);
        this.sessionDate = validateSessionDate(sessionDate);
    }

    public void cancel() {
        this.status = SessionStatus.CANCELLED;
    }

    public void updateStatus(SessionStatus status) {
        if (status == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 상태는 필수입니다.");
        }
        this.status = status;
    }

    public boolean isCancelled() {
        return status == SessionStatus.CANCELLED;
    }

    public void validateInProgressForAttendance() {
        if (status != SessionStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.SESSION_NOT_IN_PROGRESS, "진행 중인 세션이 아닙니다.");
        }
    }

    private static Cohort validateCohort(Cohort cohort) {
        if (cohort == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션은 기수 정보가 필요합니다.");
        }
        return cohort;
    }

    private static SessionType validateType(SessionType type) {
        if (type == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 타입은 필수입니다.");
        }
        return type;
    }

    private static LocalDateTime validateSessionDate(LocalDateTime sessionDate) {
        if (sessionDate == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 일시는 필수입니다.");
        }
        return sessionDate;
    }

    private static String validateDescription(String description) {
        if (description == null) {
            return null;
        }
        String normalized = description.trim();
        if (normalized.length() > 5000) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "세션 설명 길이는 5000자를 초과할 수 없습니다.");
        }
        return normalized;
    }
}
