package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class ValidationResultTest {
    @Test
    void givenNoViolation_whenThrowExceptionIfViolated_thenNothingThrown() {
        Set<Violation> violations = new HashSet<>();
        ValidationResult result = new ValidationResult(violations);
        Assertions.assertDoesNotThrow(result::throwExceptionIfViolated);
    }

    @Test
    void givenViolation_whenThrowExceptionIfViolated_thenThrowException() {
        Set<Violation> violations = new HashSet<>();
        violations.add(new Violation("Invalid Payment", "payment"));
        ValidationResult result = new ValidationResult(violations);
        Assertions.assertThrows(SystemViolationException.class, result::throwExceptionIfViolated);
    }
}
