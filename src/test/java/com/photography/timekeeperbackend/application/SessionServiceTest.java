package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.session.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SessionServiceTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private SessionRepository sessionRepository;

    @DisplayName("취소된 일정은 수정할 수 없다")
    @Test
    void updateRejectedForCancelledSession() {
        Session session = Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1));
        Session saved = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        sessionService.delete(new SessionDtos.DeleteCommand(saved.getId()));

        assertThatThrownBy(() -> sessionService.update(new SessionDtos.UpdateCommand(saved.getId(), "new", "new", SessionType.NETWORKING, LocalDateTime.now().plusDays(2))))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SESSION_CANCELLED);
    }

    @DisplayName("일정 삭제는 CANCELLED 상태로 전환한다")
    @Test
    void deleteMarksSessionCancelled() {
        Session session = Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1));
        Session saved = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();

        sessionService.delete(new SessionDtos.DeleteCommand(saved.getId()));

        Session found = sessionService.findById(new SessionDtos.FindByIdCommand(saved.getId())).session();
        assertThat(found.getStatus()).isEqualTo(SessionStatus.CANCELLED);
    }

    @DisplayName("하드 삭제는 실제 레코드를 제거한다")
    @Test
    void deleteHardRemovesRow() {
        Session session = Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1));
        Session saved = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();

        sessionService.deleteHard(new SessionDtos.DeleteHardCommand(saved.getId()));

        assertThat(sessionRepository.findById(saved.getId())).isEmpty();
    }
}
