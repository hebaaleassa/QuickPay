package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

// Techniques: equivalence partitioning (null/blank, valid, too long) + boundary value analysis on length (34 / 35)
class SenderAccountValidatorTest {
    private final SenderAccountValidator validator = new SenderAccountValidator();

    @Test
    void givenValidSender_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void givenNullOrBlankSender_whenValidate_thenViolationReturned(String sender) {
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount(sender);
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("senderAccount must not be null or blank", violations.get(0).message());
        Assertions.assertEquals("senderAccount", violations.get(0).violator());
    }

    @Test
    void givenSenderAtMaxLength_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount("A".repeat(34));
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }

    @Test
    void givenSenderAboveMaxLength_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount("A".repeat(35));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("senderAccount must not be more than 34 chars", violations.get(0).message());
        Assertions.assertEquals("senderAccount", violations.get(0).violator());
    }
}
