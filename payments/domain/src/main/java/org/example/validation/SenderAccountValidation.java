package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class SenderAccountValidation implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        if (payment.getSenderAccount() == null) {
            violations.add(new Violation("Sender account is null", "Sender account"));
        } else {
            if (payment.getSenderAccount().isBlank() || payment.getSenderAccount().isEmpty()) {
                violations.add(new Violation("Sender account is blank", "Sender account"));
            }
            if (payment.getSenderAccount().length() > 34) {
                violations.add(new Violation("Sender account length is too long, more than 34", "Sender account"));
            }
        }
        return violations;
    }

}
