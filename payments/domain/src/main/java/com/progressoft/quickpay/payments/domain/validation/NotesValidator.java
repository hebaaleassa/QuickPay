package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotesValidator implements Validator<Payment> {

    @Override
    public Optional<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        String notes = payment.getNotes();
        if (notes != null && notes.trim().length() > 500) {
            return Optional.of(new Violation("notes length should be less than 500", "NotesValidator"));
        }
        return Optional.empty();
    }
}