package com.photography.timekeeperbackend.application;

import com.photography.timekeeperbackend.application.dto.QRCodeDtos;
import lombok.RequiredArgsConstructor;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import com.photography.timekeeperbackend.domain.model.qrcode.QRCode;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.repository.qrcode.QRCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;

    @Transactional
    public QRCodeDtos.Item create(QRCodeDtos.CreateCommand command) {
        qrCodeRepository.findActiveBySessionId(command.session().getId(), LocalDateTime.now())
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.QR_ALREADY_ACTIVE, "이미 활성화된 QR 코드가 존재합니다.");
                });
        return new QRCodeDtos.Item(qrCodeRepository.save(QRCode.issue(command.session().getId(), LocalDateTime.now())));
    }

    public QRCodeDtos.Item findByHashValue(QRCodeDtos.FindByHashValueCommand command) {
        QRCode qrCode = qrCodeRepository.findByHashValue(command.hashValue())
                .orElseThrow(() -> new BusinessException(ErrorCode.QR_INVALID, "유효하지 않은 QR 입니다."));
        return new QRCodeDtos.Item(qrCode);
    }

    public QRCodeDtos.Item findById(QRCodeDtos.FindByIdCommand command) {
        QRCode qrCode = qrCodeRepository.findById(command.qrCodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QR_NOT_FOUND, "QR 코드를 찾을 수 없습니다."));
        return new QRCodeDtos.Item(qrCode);
    }

    @Transactional
    public QRCodeDtos.Item renew(QRCodeDtos.RenewCommand command) {
        QRCode current = qrCodeRepository.findById(command.qrCodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QR_NOT_FOUND, "QR 코드를 찾을 수 없습니다."));
        current.expireNow(LocalDateTime.now());
        return new QRCodeDtos.Item(qrCodeRepository.save(QRCode.issue(current.getSessionId(), LocalDateTime.now())));
    }

    public QRCodeDtos.Item validate(QRCodeDtos.ValidateCommand command) {
        QRCode qrCode = qrCodeRepository.findByHashValue(command.hashValue())
                .orElseThrow(() -> new BusinessException(ErrorCode.QR_INVALID, "유효하지 않은 QR 입니다."));
        if (qrCode.isExpired(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.QR_EXPIRED, "만료된 QR 코드입니다.");
        }
        return new QRCodeDtos.Item(qrCode);
    }
}
