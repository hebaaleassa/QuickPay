package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrencyValidatorTest {
    private final CurrencyValidator validator = new CurrencyValidator();

    @Test
    void givenValidCurrency_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNullCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency(null);
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenBlankCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency(" ");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenLowercaseCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency("jod");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }
}