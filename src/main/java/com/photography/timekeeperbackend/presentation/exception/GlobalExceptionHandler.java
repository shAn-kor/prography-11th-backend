package com.photography.timekeeperbackend.presentation.exception;

import com.photography.timekeeperbackend.presentation.dto.common.ErrorResponse;
import com.photography.timekeeperbackend.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case LOGIN_ID_DUPLICATED, ATTENDANCE_ALREADY_CHECKED, QR_ALREADY_ACTIVE -> HttpStatus.CONFLICT;
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
}
