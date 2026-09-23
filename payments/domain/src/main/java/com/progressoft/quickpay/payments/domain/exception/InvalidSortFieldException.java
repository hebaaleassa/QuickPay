package com.progressoft.quickpay.payments.domain.exception;

public class InvalidSortFieldException extends RuntimeException {
    public InvalidSortFieldException(String message) {
        super("Invalid sort field: " + message);
    }
}
