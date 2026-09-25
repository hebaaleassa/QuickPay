package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;


public class SenderAccountValidator implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {
        String sender = payment.getSenderAccount();
        if (sender == null || sender.isBlank()) {
            return List.of(new Violation("senderAccount must not be null or blank", "senderAccount"));
        }
        if (sender.trim().length() > 34) {
            return List.of(new Violation("senderAccount must not be more than 34 chars", "senderAccount"));
        }
        return List.of();
    }
}