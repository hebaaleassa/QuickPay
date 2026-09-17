package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class AmountValidatorTest {
    private final AmountValidator validator = new AmountValidator();

    @Test
    void givenValidAmount_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNullAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(null);
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must not be null", violations.get(0).message());
        Assertions.assertEquals("AmountValidator", violations.get(0).violator());
    }

    @Test
    void givenZeroAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("0.00"));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must be greater than zero", violations.get(0).message());
        Assertions.assertEquals("AmountValidator", violations.get(0).violator());
    }

    @Test
    void givenNegativeAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("-50"));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must be greater than zero", violations.get(0).message());
        Assertions.assertEquals("AmountValidator", violations.get(0).violator());
    }
}