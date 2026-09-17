package org.example.validation;

import org.example.exception.SystemViolationException;
import org.example.model.Violation;

import java.util.Set;

public class ValidationResult {
    private final Set<Violation> violations;

    public ValidationResult(Set<Violation> violations) {
        this.violations = violations;
    }

    public void throwExceptionIfViolated() {
        if (!violations.isEmpty()) {
            throw new SystemViolationException(violations);
        }
    }
}
