package com.photography.timekeeperbackend.presentation.dto.qrcode;

import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;

import java.time.LocalDateTime;

public record QRCodeResponse(String sessionTitle, String hashValue, LocalDateTime expiresAt) {
    public static QRCodeResponse from(QRCode qrCode, String sessionTitle) {
        return new QRCodeResponse(sessionTitle, qrCode.getHashValue(), qrCode.getExpiresAt());
    }
}
