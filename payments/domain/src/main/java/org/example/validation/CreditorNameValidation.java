package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreditorNameValidation implements Validator<Payment> {

    public List<Violation> validate(Payment payment) {

        List<Violation> violations = new ArrayList<>();
        if (payment.getCreditorName() == null) {
            violations.add(new Violation("creditor Name is null", "creditor Name"));
        } else {
            if (payment.getCreditorName().isBlank() || payment.getCreditorName().isEmpty()) {
                violations.add(new Violation("creditor Name is blank", "creditor Name"));
            }
            if (payment.getCreditorName().length() > 100) {
                violations.add(new Violation("creditor Name length is too long, more than 100", "creditor Name"));
            }
            String[] name = payment.getCreditorName().trim().split(" ");
            if (name.length != 2) {
                violations.add(new Violation("creditor Name must be two parts", "creditor Name"));
            }
            if (!(name.length == 2 && Arrays.stream(name).
                    allMatch(part -> part.matches("^[a-zA-Z0-9]+$")))) {
                violations.add(new Violation("creditor Name must be alphanumeric only", "creditor Name"));
            }
        }
        return violations;
    }
}
