package org.example.validation;

import org.example.model.Violation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ValidatorChain <T>{
    private final List<Validator<T>> validators;

    public ValidatorChain(List<Validator<T>> validators) {
        this.validators = validators;
    }

    public ValidationResult validate(T t) {
        Set<Violation> all = new LinkedHashSet<>();
        for (Validator<T> validator : validators) {
            all.addAll(validator.validate(t));
        }
        return new ValidationResult(all);
    }
}
