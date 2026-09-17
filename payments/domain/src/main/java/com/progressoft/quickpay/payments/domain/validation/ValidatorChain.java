package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ValidatorChain<T> {
    private final List<Validator<T>> validators;

    public ValidatorChain(List<Validator<T>> validators) {
        this.validators = validators;
    }

    public ValidationResult validate(T value) {
        Set<Violation> violations = new LinkedHashSet<>();
        for (Validator<T> validator : validators) {
            validator.validate(value).ifPresent(
                    violations::add
            );
        }
        return new ValidationResult(violations);
    }
}