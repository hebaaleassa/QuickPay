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
        Assertions.assertEquals("CreditorNameValidator", violations.get(0).violator());
    }

    @Test
    void givenOneWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("CreditorNameValidator", violations.get(0).violator());
    }

    @Test
    void givenThreeWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay Kamal Khamis");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("CreditorNameValidator", violations.get(0).violator());
    }

    @Test
    void givenSpecialCharacter_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay K!");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("creditor name must be 2 Alphanumeric parts", violations.get(0).message());
        Assertions.assertEquals("CreditorNameValidator", violations.get(0).violator());
    }
}