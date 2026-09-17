package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmountValidator implements Validator<Payment> {
    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        BigDecimal amount = payment.getAmount();
        if (amount == null) {

            violations.add(new Violation("amount must not be null", "AmountValidator"));
            return violations;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            violations.add(new Violation("amount must be greater than zero", "AmountValidator"));
            return violations;
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            violations.add(new Violation("amount must have maximum 2 decimal places", "AmountValidator"));
            return violations;
        }
        return violations;
    }
}