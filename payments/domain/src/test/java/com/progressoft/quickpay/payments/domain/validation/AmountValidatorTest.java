package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenZeroAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("0.00"));
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenNegativeAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("-50"));
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }
}