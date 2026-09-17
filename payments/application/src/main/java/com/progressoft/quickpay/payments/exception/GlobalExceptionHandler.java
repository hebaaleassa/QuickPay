package com.progressoft.quickpay.payments.exception;

import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SystemViolationException.class)
    public ResponseEntity<?> handleException(Exception exception)
    {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
