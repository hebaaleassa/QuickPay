package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;

public class NotesValidator implements Validator<Payment> {

    private final int maxLength;

    public NotesValidator(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public List<Violation> validate(Payment payment) {
        String notes = payment.getNotes();
        if (notes != null && notes.trim().length() > maxLength) {
            return List.of(new Violation("notes must not be more than " + maxLength + " chars", "notes"));
        }
        return List.of();
    }
}