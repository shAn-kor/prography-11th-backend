package com.photography.timekeeperbackend.infrastructure.repository.adapter;

import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.repository.qrcode.QRCodeRepository;
import com.photography.timekeeperbackend.infrastructure.repository.jpa.JpaQRCodeRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository



@RequiredArgsConstructor
public class QRCodeRepositoryImpl implements QRCodeRepository {

    private final JpaQRCodeRepository jpaRepository;

    @Override
    public Optional<QRCode> findByHashValue(String hashValue) { return jpaRepository.findByHashValue(hashValue); }
    @Override
    public Optional<QRCode> findById(Long id) { return jpaRepository.findById(id); }
    @Override
    public Optional<QRCode> findActiveBySessionId(Long sessionId, LocalDateTime now) {
        return jpaRepository.findTopBySessionIdAndExpiresAtAfterOrderByIdDesc(sessionId, now);
    }
    @Override
    public QRCode save(QRCode qrCode) { return jpaRepository.save(qrCode); }
}
