package org.example.validation;

import lombok.Value;
import lombok.extern.apachecommons.CommonsLog;
import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class NotesValidation implements Validator<Payment> {

    private final Integer maxLength;

    public NotesValidation(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public List<Violation> validate(Payment payment) {

        List<Violation> violations = new ArrayList<>();
        if (payment.getNotes() != null) {
            if (payment.getNotes().length() > maxLength) {
                violations.add(new Violation("the length is longer than 500", "notes"));
            }
        }
        return violations;
    }
}
