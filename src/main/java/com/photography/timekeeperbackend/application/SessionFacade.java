package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.CohortDtos;
import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionFacade {

    private final SessionService sessionService;
    private final CohortService cohortService;
    private final QRCodeService qrCodeService;

    public SessionDtos.Item createSession(SessionDtos.CreateFacadeCommand command) {
        Session session = Session.create(
                cohortService.findCurrentCohort().cohort(),
                command.title(),
                command.description(),
                command.type(),
                command.sessionDate()
        );
        Session savedSession = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        try {
            qrCodeService.create(new QRCodeDtos.CreateCommand(savedSession));
            return new SessionDtos.Item(savedSession);
        } catch (RuntimeException ex) {
            sessionService.deleteHard(new SessionDtos.DeleteHardCommand(savedSession.getId()));
            throw ex;
        }
    }

    public QRCodeDtos.Item createQrCode(QRCodeDtos.CreateQrCodeCommand command) {
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(command.sessionId())).session();
        return qrCodeService.create(new QRCodeDtos.CreateCommand(session));
    }
}
