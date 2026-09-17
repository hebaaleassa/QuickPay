package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AmountValidator implements Validator<Payment> {
    @Override
    public Optional<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        BigDecimal amount = payment.getAmount();
        if (amount == null) {

            return Optional.of(new Violation("amount must not be null", "AmountValidator"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of(new Violation("amount must be greater than zero", "AmountValidator"));
        }
        if (Math.max(amount.stripTrailingZeros().scale(), 0) > 2) {
            return Optional.of(new Violation("amount must have maximum 2 decimal places", "AmountValidator"));
        }
        return Optional.empty();
    }
}