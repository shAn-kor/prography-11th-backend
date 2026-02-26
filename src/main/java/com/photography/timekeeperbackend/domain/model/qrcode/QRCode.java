package com.photography.timekeeperbackend.domain.model.qrcode;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * - QRCode 엔티티: 세션 출석용 QR 해시와 만료 시각을 관리한다.
 */
@Entity
@Table(name = "qr_code")
public class QRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(nullable = false, unique = true, length = 36)
    private String hashValue;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected QRCode() {
    }

    public static QRCode issue(Long sessionId, LocalDateTime now) {
        if (sessionId == null || sessionId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "sessionId는 1 이상이어야 합니다.");
        }
        if (now == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "QR 발급 시각은 필수입니다.");
        }
        QRCode qrCode = new QRCode();
        qrCode.sessionId = sessionId;
        qrCode.hashValue = UUID.randomUUID().toString();
        qrCode.expiresAt = now.plusHours(24);
        return qrCode;
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public String getHashValue() { return hashValue; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public boolean isExpired(LocalDateTime now) {
        if (now == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "현재 시각은 필수입니다.");
        }
        return now.isAfter(expiresAt);
    }

    public void expireNow(LocalDateTime now) {
        if (now == null) {
            throw new BusinessException(ErrorCode.INVALID_DOMAIN_VALUE, "만료 시각은 필수입니다.");
        }
        this.expiresAt = now;
    }
}
