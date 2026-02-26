package com.photography.timekeeperbackend.domain.model.qrcode;

import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QRCodeTest {

    @DisplayName("QR 발급 시 24시간 뒤 만료 시각을 가진다")
    @Test
    void issueSets24HoursExpiry() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 26, 19, 0);

        QRCode qrCode = QRCode.issue(1L, now);

        assertThat(qrCode.getSessionId()).isEqualTo(1L);
        assertThat(qrCode.getExpiresAt()).isEqualTo(now.plusHours(24));
        assertThat(qrCode.getHashValue()).isNotBlank();
    }

    @DisplayName("현재 시간이 만료 시각과 같으면 만료되지 않은 것으로 본다")
    @Test
    void isExpiredIsFalseWhenEqual() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 26, 19, 0);
        QRCode qrCode = QRCode.issue(1L, now);

        boolean expired = qrCode.isExpired(now.plusHours(24));

        assertThat(expired).isFalse();
    }

    @DisplayName("expireNow 호출 시 만료 시각이 즉시 갱신된다")
    @Test
    void expireNowUpdatesExpiresAt() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 26, 19, 0);
        QRCode qrCode = QRCode.issue(1L, now);
        LocalDateTime forcedExpireAt = now.plusHours(1);

        qrCode.expireNow(forcedExpireAt);

        assertThat(qrCode.getExpiresAt()).isEqualTo(forcedExpireAt);
        assertThat(qrCode.isExpired(forcedExpireAt.plusSeconds(1))).isTrue();
    }

    @DisplayName("sessionId가 0 이하이면 QR 발급 시 INVALID_DOMAIN_VALUE 예외가 발생한다")
    @Test
    void issueFailsWhenSessionIdInvalid() {
        assertThatThrownBy(() -> QRCode.issue(0L, LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_DOMAIN_VALUE);
    }
}
