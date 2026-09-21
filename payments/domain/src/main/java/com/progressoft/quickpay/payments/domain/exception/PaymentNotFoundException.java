package com.progressoft.quickpay.payments.domain.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Payment not found with this id: " + id);
    }
}
