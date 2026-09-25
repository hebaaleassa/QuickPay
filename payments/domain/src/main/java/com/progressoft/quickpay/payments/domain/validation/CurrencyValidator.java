package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.List;

public class CurrencyValidator implements Validator<Payment> {
    private final List<String> allowedCurrencies;

    public CurrencyValidator(List<String> allowedCurrencies) {
        this.allowedCurrencies = allowedCurrencies;
    }

    @Override
    public List<Violation> validate(Payment payment) {
        String currency = payment.getCurrency();
        if (currency == null || currency.isBlank()) {
            return List.of(new Violation("currency must not be null or blank",
                    "currency"));
        }

        if (!allowedCurrencies.contains(currency)) {
            return List.of(new Violation("currency is not supported", "currency"));
        }
        return List.of();
    }
}
