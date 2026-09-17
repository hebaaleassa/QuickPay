package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;

import java.util.Optional;

public class CreditorNameValidator implements Validator<Payment>{
    public Optional<Violation> validate(Payment payment)
    {
        String name = payment.getCreditorName();
        if(name == null || name.isBlank()){
            return Optional.of(new Violation("creditorName must not be blank or null", "CreditorNameValidator"));
        }
        if(name.length()>100) {return Optional.of(new Violation("creditorName must not be more than 100 chars", "CreditorNameValidator"));
        }
        String[] split_parts=name.split(" ",-1);
        if(split_parts.length != 2
                || !isAlphaNumeric(split_parts[0])
                || !isAlphaNumeric(split_parts[1])){
            return Optional.of(new Violation("creditor name must be 2 Alphanumeric parts","CreditorNameValidator"));
        }
        return Optional.empty();
    }
    private boolean isAlphaNumeric(String name)
    {
        return name.matches("[A-Za-z0-9]+");
    }
}
