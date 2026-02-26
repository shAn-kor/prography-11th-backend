package com.photography.timekeeperbackend.infrastructure.repository.jpa;

import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaQRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByHashValue(String hashValue);
    Optional<QRCode> findTopBySessionIdAndExpiresAtAfterOrderByIdDesc(Long sessionId, LocalDateTime now);
}
