package com.photography.timekeeperbackend.domain.repository.qrcode;

import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface QRCodeRepository {
    Optional<QRCode> findByHashValue(String hashValue);
    Optional<QRCode> findById(Long id);
    Optional<QRCode> findActiveBySessionId(Long sessionId, LocalDateTime now);
    QRCode save(QRCode qrCode);
}
