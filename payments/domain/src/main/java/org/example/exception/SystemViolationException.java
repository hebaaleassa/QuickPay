package org.example.exception;

import org.example.model.Violation;

import java.util.Set;

public class SystemViolationException extends RuntimeException {

    private final Set<Violation> violations;

    public SystemViolationException(Set<Violation> violations) {

        this.violations = violations;
    }

    public Set<Violation> getViolations() {
        return violations;
    }
}
