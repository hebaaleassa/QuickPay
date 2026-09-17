package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenOneWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenThreeWordName_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay Kamal Khamis");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenSpecialCharacter_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setCreditorName("Tolay K!");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }
}