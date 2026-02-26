package com.photography.timekeeperbackend.domain.model.session;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionTest {

    @DisplayName("세션 생성 시 상태는 SCHEDULED다")
    @Test
    void createSetsScheduledStatus() {
        Session session = Session.create(Cohort.of(11, true), "title", "desc", SessionType.STUDY, LocalDateTime.now());

        assertThat(session.getStatus()).isEqualTo(SessionStatus.SCHEDULED);
    }

    @DisplayName("cancel 호출 시 상태가 CANCELLED로 변경된다")
    @Test
    void cancelChangesStatus() {
        Session session = Session.create(Cohort.of(11, true), "title", "desc", SessionType.STUDY, LocalDateTime.now());

        session.cancel();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.CANCELLED);
        assertThat(session.isCancelled()).isTrue();
    }

    @DisplayName("진행 중이 아닌 세션은 출석 검증 시 예외가 발생한다")
    @Test
    void validateInProgressForAttendanceThrowsWhenNotInProgress() {
        Session session = Session.create(Cohort.of(11, true), "title", "desc", SessionType.STUDY, LocalDateTime.now());

        assertThatThrownBy(session::validateInProgressForAttendance)
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SESSION_NOT_IN_PROGRESS);
    }

    @DisplayName("상태를 IN_PROGRESS로 바꾸면 출석 검증을 통과한다")
    @Test
    void validateInProgressForAttendancePassesWhenInProgress() {
        Session session = Session.create(Cohort.of(11, true), "title", "desc", SessionType.STUDY, LocalDateTime.now());
        session.updateStatus(SessionStatus.IN_PROGRESS);

        session.validateInProgressForAttendance();

        assertThat(session.getStatus()).isEqualTo(SessionStatus.IN_PROGRESS);
    }

    @DisplayName("빈 제목으로 세션 생성 시 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void createFailsWhenTitleBlank() {
        assertThatThrownBy(() -> Session.create(Cohort.of(11, true), " ", "desc", SessionType.STUDY, LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }

    @DisplayName("세션 상태를 null로 변경하면 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void updateStatusFailsWhenNull() {
        Session session = Session.create(Cohort.of(11, true), "title", "desc", SessionType.STUDY, LocalDateTime.now());

        assertThatThrownBy(() -> session.updateStatus(null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
