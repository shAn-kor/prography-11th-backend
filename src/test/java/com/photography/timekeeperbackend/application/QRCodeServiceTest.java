package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
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
class QRCodeServiceTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CohortService cohortService;

    @DisplayName("활성 QR이 있으면 새 QR 생성을 거부한다")
    @Test
    void createRejectsWhenActiveQrExists() {
        Session session = Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1));
        Session savedSession = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        qrCodeService.create(new QRCodeDtos.CreateCommand(savedSession));

        assertThatThrownBy(() -> qrCodeService.create(new QRCodeDtos.CreateCommand(savedSession)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.QR_ALREADY_ACTIVE);
    }

    @DisplayName("QR 생성 후 validate로 동일 QR을 조회할 수 있다")
    @Test
    void validateReturnsCreatedQr() {
        Session session = Session.create(cohortService.findCurrentCohort().cohort(), "title", "desc", SessionType.STUDY, LocalDateTime.now().plusDays(1));
        Session savedSession = sessionService.create(new SessionDtos.CreateServiceCommand(session)).session();
        QRCode created = qrCodeService.create(new QRCodeDtos.CreateCommand(savedSession)).qrCode();

        QRCode validated = qrCodeService.validate(new QRCodeDtos.ValidateCommand(created.getHashValue())).qrCode();

        assertThat(validated.getId()).isEqualTo(created.getId());
    }
}
