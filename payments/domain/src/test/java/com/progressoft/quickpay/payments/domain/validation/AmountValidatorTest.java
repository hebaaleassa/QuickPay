package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        Assertions.assertEquals("amount", violations.get(0).violator());
    }

    @Test
    void givenZeroAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("0.00"));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must be greater than zero", violations.get(0).message());
        Assertions.assertEquals("amount", violations.get(0).violator());
    }

    @Test
    void givenNegativeAmount_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal("-50"));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must be greater than zero", violations.get(0).message());
        Assertions.assertEquals("amount", violations.get(0).violator());
    }

    // BVA on scale (2 / 3 decimals) and integer digits (36 / 37), plus representations that normalize to valid values
    @ParameterizedTest
    @ValueSource(strings = {"0.01", "10.100", "1E+3", "999999999999999999999999999999999999.99"})
    void givenAmountWithinScaleAndDigitLimits_whenValidate_thenNoViolation(String amount) {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal(amount));
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.005", "0.001", "1.0050"})
    void givenAmountWithMoreThanTwoDecimals_whenValidate_thenViolationReturned(String amount) {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal(amount));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must have maximum 2 decimal places", violations.get(0).message());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1000000000000000000000000000000000000", "1E+40"})
    void givenAmountWithMoreThan36IntegerDigits_whenValidate_thenViolationReturned(String amount) {
        Payment payment = PaymentTestData.validPayment();
        payment.setAmount(new BigDecimal(amount));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("amount must not have more than 36 integer digits", violations.get(0).message());
        Assertions.assertEquals("amount", violations.get(0).violator());
    }
}
