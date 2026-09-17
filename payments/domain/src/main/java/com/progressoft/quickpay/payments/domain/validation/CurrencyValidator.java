package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.List;

public class CurrencyValidator implements Validator<Payment> {
    @Override
    public List<Violation> validate(Payment payment) {
        String currency = payment.getCurrency();
        if (currency == null || currency.isBlank()) {
            return List.of(new Violation("currency must not be null or blank",
                    "CurrencyValidator"));
        }

        if (currency.length() != 3 || !currency.chars().allMatch(c -> c >= 'A' && c <= 'Z')) {
            return List.of(new Violation(
                    "currency must be 3 upper case characters", "CurrencyValidator"
            ));
        }
        return List.of();
    }
}
