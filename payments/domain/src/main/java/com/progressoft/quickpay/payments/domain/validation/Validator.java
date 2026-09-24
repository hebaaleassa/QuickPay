package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.List;

public interface Validator<T> {
    List<Violation> validate(T value);
}