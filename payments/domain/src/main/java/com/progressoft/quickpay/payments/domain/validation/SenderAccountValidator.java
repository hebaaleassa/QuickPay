package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SenderAccountValidator implements Validator<Payment> {

    @Override
    public Optional<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        String sender = payment.getSenderAccount();
        if (sender == null || sender.isBlank()) {
            return Optional.of(new Violation("senderAccount can't be blank or null", "senderAccount"));
        }
        if (sender.trim().length() > 34) {
            return Optional.of(new Violation("sender must be at most 35 characters", "senderAccountValidator"));
        }
        return Optional.empty();
    }
}