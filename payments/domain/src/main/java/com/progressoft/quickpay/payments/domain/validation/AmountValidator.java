package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmountValidator implements Validator<Payment> {
    public static final int MAX_SCALE = 2;
    public static final int MAX_INTEGER_DIGITS = 36;

    @Override
    public List<Violation> validate(Payment payment) {
        List<Violation> violations = new ArrayList<>();
        BigDecimal amount = payment.getAmount();
        if (amount == null) {

            violations.add(new Violation("amount must not be null", "amount"));
            return violations;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            violations.add(new Violation("amount must be greater than zero", "amount"));
            return violations;
        }
        if (amount.stripTrailingZeros().scale() > MAX_SCALE) {
            violations.add(new Violation("amount must have maximum 2 decimal places", "amount"));
            return violations;
        }
        if (amount.precision() - amount.scale() > MAX_INTEGER_DIGITS) {
            violations.add(new Violation("amount must not have more than " + MAX_INTEGER_DIGITS + " integer digits", "amount"));
            return violations;
        }
        return violations;
    }
}
