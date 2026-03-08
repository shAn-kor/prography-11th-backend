package com.photography.timekeeperbackend.presentation.exception;

import com.photography.timekeeperbackend.presentation.dto.common.ErrorResponse;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import com.photography.timekeeperbackend.domain.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case LOGIN_ID_DUPLICATED, ATTENDANCE_ALREADY_CHECKED, QR_ALREADY_ACTIVE -> HttpStatus.CONFLICT;
            case CONCURRENCY_CONFLICT -> HttpStatus.CONFLICT;
            case DEPOSIT_INSUFFICIENT,
                 EXCUSE_LIMIT_EXCEEDED,
                 MEMBER_WITHDRAWN,
                 SESSION_NOT_IN_PROGRESS,
                 SESSION_CANCELLED,
                 QR_EXPIRED,
                 PASSWORD_NOT_MATCHED,
                 INVALID_DOMAIN_VALUE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.NOT_FOUND;
        };
        return ResponseEntity.status(status).body(new ErrorResponse(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause() == null ? "" : ex.getMostSpecificCause().getMessage();
        String lower = message == null ? "" : message.toLowerCase();
        if (lower.contains("uk_session_cohort_member")
                || lower.contains("attendance")
                && lower.contains("session_id")
                && lower.contains("cohort_member_id")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorCode.ATTENDANCE_ALREADY_CHECKED, "이미 출석 체크가 완료되었습니다."));
        }
        if (lower.contains("member")
                && lower.contains("login_id")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(ErrorCode.LOGIN_ID_DUPLICATED, "이미 사용 중인 loginId 입니다."));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorCode.CONCURRENCY_CONFLICT, "동시 요청 충돌이 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorCode.CONCURRENCY_CONFLICT, "동시 요청 충돌이 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }
}
