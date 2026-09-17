package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.Optional;

public interface Validator<T> {
    Optional<Violation> validate(T value);
}