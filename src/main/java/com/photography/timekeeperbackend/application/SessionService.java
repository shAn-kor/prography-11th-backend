package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionStatus;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionDtos.Item findById(SessionDtos.FindByIdCommand command) {
        Session session = sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND, "일정을 찾을 수 없습니다."));
        return new SessionDtos.Item(session);
    }

    public SessionDtos.Items findAllForAdmin() {
        return new SessionDtos.Items(sessionRepository.findAll());
    }

    public SessionDtos.Items findAllForMember() {
        return new SessionDtos.Items(sessionRepository.findAllByStatusNot(SessionStatus.CANCELLED));
    }

    @Transactional
    public SessionDtos.Item create(SessionDtos.CreateServiceCommand command) {
        return new SessionDtos.Item(sessionRepository.save(command.session()));
    }

    @Transactional
    public SessionDtos.Item update(SessionDtos.UpdateCommand command) {
        Session session = sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND, "일정을 찾을 수 없습니다."));
        if (session.isCancelled()) {
            throw new BusinessException(ErrorCode.SESSION_CANCELLED, "취소된 일정은 수정할 수 없습니다.");
        }
        session.update(command.title(), command.description(), command.type(), command.sessionDate());
        return new SessionDtos.Item(session);
    }

    @Transactional
    public void delete(SessionDtos.DeleteCommand command) {
        Session session = sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND, "일정을 찾을 수 없습니다."));
        session.cancel();
    }

    @Transactional
    public SessionDtos.Item updateStatus(SessionDtos.UpdateStatusCommand command) {
        Session session = sessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND, "일정을 찾을 수 없습니다."));
        session.updateStatus(command.status());
        return new SessionDtos.Item(session);
    }

    @Transactional
    public void deleteHard(SessionDtos.DeleteHardCommand command) {
        sessionRepository.deleteById(command.sessionId());
    }
}
