package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class ReceiverAccountValidation implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        if (payment.getReceiverAccount() == null) {
            violations.add(new Violation("receiver Account is null", "receiver Account"));
        } else {
            if (payment.getReceiverAccount().isBlank() || payment.getReceiverAccount().isEmpty()) {
                violations.add(new Violation("receiver Account is blank", "receiver Account"));
            }
            if (payment.getReceiverAccount().length() > 34) {
                violations.add(new Violation("receiver Account length is too long", "receiver Account"));
            }
            if (payment.getReceiverAccount().equals(payment.getSenderAccount())) {
                violations.add(new Violation("receiver Account is the same as sender Account", "receiver Account"));
            }
        }
        return violations;
    }

}
