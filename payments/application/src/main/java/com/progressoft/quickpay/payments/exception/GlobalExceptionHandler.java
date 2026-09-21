package com.progressoft.quickpay.payments.exception;

import com.progressoft.quickpay.payments.domain.exception.PaymentNotFoundException;
import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.training.fileparser.exception.FileParserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SystemViolationException.class)
    public ResponseEntity<String> handleException(SystemViolationException exception) {
        log.error("Validation failure", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(value = FileParserException.class)
    public ResponseEntity<String> handleFileParserException(FileParserException exception) {
        log.error("File parser failure", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePaymentNotFound(PaymentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
