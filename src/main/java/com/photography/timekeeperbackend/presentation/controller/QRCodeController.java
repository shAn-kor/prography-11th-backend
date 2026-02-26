package com.photography.timekeeperbackend.presentation.controller;

import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import com.photography.timekeeperbackend.application.dto.SessionDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.application.QRCodeService;
import com.photography.timekeeperbackend.application.SessionFacade;
import com.photography.timekeeperbackend.application.SessionService;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.presentation.dto.qrcode.QRCodeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QRCodeController {

    private final SessionFacade sessionFacade;
    private final QRCodeService qrCodeService;
    private final SessionService sessionService;

    @PostMapping("/admin/sessions/{id}/qrcodes")
    public ResponseEntity<QRCodeResponse> createQRCode(@PathVariable Long id) {
        QRCode qrCode = sessionFacade.createQrCode(new QRCodeDtos.CreateQrCodeCommand(id)).qrCode();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(qrCode.getSessionId())).session();
        return ResponseEntity.status(HttpStatus.CREATED).body(QRCodeResponse.from(qrCode, session.getTitle()));
    }

    @PutMapping("/admin/qrcodes/{id}")
    public ResponseEntity<QRCodeResponse> renewQRCode(@PathVariable Long id) {
        QRCode qrCode = qrCodeService.renew(new QRCodeDtos.RenewCommand(id)).qrCode();
        Session session = sessionService.findById(new SessionDtos.FindByIdCommand(qrCode.getSessionId())).session();
        return ResponseEntity.ok(QRCodeResponse.from(qrCode, session.getTitle()));
    }
}
