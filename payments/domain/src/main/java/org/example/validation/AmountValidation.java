package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmountValidation implements Validator<Payment> {

    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        if (payment.getAmount() == null) {
            violations.add(new Violation("Amount is null", "amount"));
        } else {
            if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                violations.add(new Violation("Amount is not positive", "amount"));
            }
            if (payment.getAmount().scale() > 2) {
                violations.add(new Violation("Amount scale is more than two", "amount"));
            }
        }
        return violations;
    }
}
