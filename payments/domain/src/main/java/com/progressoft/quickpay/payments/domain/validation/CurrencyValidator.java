package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.Optional;

public class CurrencyValidator implements Validator<Payment> {
    @Override
    public Optional<Violation> validate(Payment payment) {
        String currency = payment.getCurrency();
        if (currency == null || currency.isBlank()) {
            return Optional.of(new Violation("currency must not be null or blank",
                    "CurrencyValidator"));
        }

        if (currency.length() != 3 || !currency.chars().allMatch(c -> c >= 'A' && c <= 'Z')) {
            return Optional.of(new Violation(
                    "currency must be 3 upper case characters", "CurrencyValidator"
            ));
        }
        return Optional.empty();
    }
}
