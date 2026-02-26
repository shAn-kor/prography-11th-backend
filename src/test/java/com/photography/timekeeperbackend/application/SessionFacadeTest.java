package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.qrcode.QRCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SessionFacadeTest {

    @Autowired
    private SessionFacade sessionFacade;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @DisplayName("세션 생성 퍼사드는 세션 생성과 QR 생성을 함께 수행한다")
    @Test
    void createSessionCreatesQrCode() {
        Session session = sessionFacade.createSession(new SessionDtos.CreateFacadeCommand(
                "session-title",
                "desc",
                SessionType.STUDY,
                LocalDateTime.now().plusDays(1)
        )).session();

        QRCode qrCode = qrCodeRepository.findActiveBySessionId(session.getId(), LocalDateTime.now()).orElseThrow();
        assertThat(session.getId()).isNotNull();
        assertThat(qrCode.getSessionId()).isEqualTo(session.getId());
    }
}
