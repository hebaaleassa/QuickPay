package com.progressoft.quickpay.payments.domain.exception;

public class InvalidPagingException extends RuntimeException {
    public InvalidPagingException(String message) {
        super(message);
    }
}
