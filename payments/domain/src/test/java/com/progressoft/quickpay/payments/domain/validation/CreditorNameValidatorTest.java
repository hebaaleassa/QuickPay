package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CreditorNameValidatorTest {
    private final CreditorNameValidator validator = new CreditorNameValidator();

    @Test
    void givenTwoWordName_whenValidate_thenSuccess() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNullName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName(null);
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals("creditorName must not be blank or null", violations.get(0).message());
        Assertions.assertEquals("creditorName", violations.get(0).violator());
    }

    @Test
    void givenOneWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("creditorName", violations.get(0).violator());
    }

    @Test
    void givenThreeWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay Kamal Khamis");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("creditorName", violations.get(0).violator());
    }

    @Test
    void givenSpecialCharacter_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay K!");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("creditorName", violations.get(0).violator());
    }

    @Test
    void givenBlankName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("   ");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals("creditorName must not be blank or null", violations.get(0).message());
    }

    @Test
    void givenNameAtMaxLength_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("a".repeat(49) + " " + "b".repeat(50));
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }

    @Test
    void givenNameAboveMaxLength_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("a".repeat(50) + " " + "b".repeat(50));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditorName must not be more than 100 chars", violations.get(0).message());
    }

    @Test
    void givenDoubleSpaceBetweenParts_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay  Khamis");
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", validator.validate(payment).get(0).message());
    }

    @Test
    void givenAlphanumericParts_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay2 K3");
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }
}
