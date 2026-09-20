package com.progressoft.quickpay.payments.domain.exception;

import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SystemViolationException extends RuntimeException {
    private final Set<Violation> violations;


    public SystemViolationException(Set<Violation> violations) {
        super(violations.stream().map(v -> "[" + v.violator() + "]" + v.message())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed"));
        this.violations = violations;
    }

    public List<String> getViolationMessage() {
        List<String> message = new ArrayList<>();
        for (Violation violation : violations) {
            message.add("[" + violation.violator() + "]" + violation.message());
        }
        return message;
    }

    public Set<Violation> getViolations() {
        return violations;
    }
}
