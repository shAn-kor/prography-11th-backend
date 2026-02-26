package com.photography.timekeeperbackend.application.dto;

import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;

public final class QRCodeDtos {

    private QRCodeDtos() {
    }

    public record CreateCommand(Session session) {}
    public record FindByHashValueCommand(String hashValue) {}
    public record FindByIdCommand(Long qrCodeId) {}
    public record RenewCommand(Long qrCodeId) {}
    public record ValidateCommand(String hashValue) {}
    public record CreateQrCodeCommand(Long sessionId) {}

    public record Item(QRCode qrCode) {}
}
