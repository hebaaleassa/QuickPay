package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class NotesValidation implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {

        List<Violation> violations = new ArrayList<>();
        if (!payment.getNotes().isEmpty()) {
            if (payment.getNotes().length() > 500) {
                violations.add(new Violation("the length is longer than 500", "notes"));
            }
        }
        return violations;
    }
}
