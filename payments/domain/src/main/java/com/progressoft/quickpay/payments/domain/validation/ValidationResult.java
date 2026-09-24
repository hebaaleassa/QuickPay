package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.Set;

public class ValidationResult {
    private final Set<Violation> violations;

    public ValidationResult(Set<Violation> violations) {
        this.violations = violations;
    }

    public Set<Violation> getViolations() {
        return violations;
    }

    public void throwExceptionIfViolated() {
        if (!violations.isEmpty()) {
            throw new SystemViolationException(violations);
        }
    }
}
