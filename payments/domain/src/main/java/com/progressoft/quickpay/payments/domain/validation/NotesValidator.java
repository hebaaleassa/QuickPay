package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;

public class NotesValidator implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        String notes = payment.getNotes();
        if (notes != null && notes.trim().length() > 500) {
            return List.of(new Violation("notes length should be less than 500", "NotesValidator"));
        }
        return List.of();
    }
}