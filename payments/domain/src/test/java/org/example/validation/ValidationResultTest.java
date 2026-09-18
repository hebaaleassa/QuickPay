package org.example.validation;

import org.example.exception.SystemViolationException;
import org.example.model.Violation;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationResultTest {

    @Test
    void givenEmptyViolations_whenThrowExceptionIfViolated_doesNotThrow() {
        ValidationResult result = new ValidationResult(Collections.emptySet());

        assertDoesNotThrow(result::throwExceptionIfViolated);
    }

    @Test
    void givenViolationsExist_whenThrowExceptionIfViolated_throwsSystemViolationException() {
        Set<Violation> violations = Set.of(new Violation("amount", "Amount is invalid"));
        ValidationResult result = new ValidationResult(violations);
        assertThrows(SystemViolationException.class, result::throwExceptionIfViolated);
    }
}