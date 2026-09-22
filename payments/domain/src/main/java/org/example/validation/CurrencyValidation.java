package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;

import java.util.ArrayList;
import java.util.List;

public class CurrencyValidation implements Validator<Payment> {

    private final List<String> currencyValues;

    public CurrencyValidation(List<String> currencyValues) {
        this.currencyValues = currencyValues;
    }

    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        if (payment.getCurrency() == null) {
            violations.add(new Violation("currency is null", "currency"));
        } else {
            if (payment.getCurrency().isBlank() || payment.getCurrency().isEmpty()) {
                violations.add(new Violation("currency is blank", "currency"));
            }
            if  (!currencyValues.contains(payment.getCurrency())) {
                violations.add(new Violation("currency does not match", "currency"));
            }
        }
        return violations;
    }
}
