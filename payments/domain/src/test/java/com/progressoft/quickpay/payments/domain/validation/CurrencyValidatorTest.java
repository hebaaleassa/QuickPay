package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CurrencyValidatorTest {
    private final CurrencyValidator validator = new CurrencyValidator(List.of("JOD", "USD", "EUR"));

    @Test
    void givenValidCurrency_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNullCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency(null);
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("currency must not be null or blank", violations.get(0).message());
        Assertions.assertEquals("currency", violations.get(0).violator());
    }

    @Test
    void givenBlankCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency(" ");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("currency must not be null or blank", violations.get(0).message());
        Assertions.assertEquals("currency", violations.get(0).violator());
    }

    @Test
    void givenLowercaseCurrency_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCurrency("jod");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("currency is not supported", violations.get(0).message());
        Assertions.assertEquals("currency", violations.get(0).violator());
    }
}