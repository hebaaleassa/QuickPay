package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecieverAccountValidatorTest {
    private final RecieverAccountValidator validator = new RecieverAccountValidator();

    @Test
    void givenValidReceiver_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNullReceiver_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount(null);
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("receiverAccount must not be null or blank", violations.get(0).message());
        Assertions.assertEquals("receiverAccount", violations.get(0).violator());
    }

    @Test
    void givenReceiverSameAsSender_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("ACC-1");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("receiverAccount must differ from senderAccount", violations.get(0).message());
        Assertions.assertEquals("receiverAccount", violations.get(0).violator());
    }

    @Test
    void givenReceiverSameAsSenderLowerCase_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("acc-1");
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("receiverAccount must differ from senderAccount", violations.get(0).message());
        Assertions.assertEquals("receiverAccount", violations.get(0).violator());
    }

    @Test
    void givenBlankReceiver_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("  ");
        Assertions.assertEquals("receiverAccount must not be null or blank", validator.validate(payment).get(0).message());
    }

    @Test
    void givenReceiverAtMaxLength_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("B".repeat(34));
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }

    @Test
    void givenReceiverAboveMaxLength_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("B".repeat(35));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("receiverAccount must not be more than 34 chars", violations.get(0).message());
        Assertions.assertEquals("receiverAccount", violations.get(0).violator());
    }

    @Test
    void givenNullSender_whenValidate_thenReceiverIsNotComparedToSender() {
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount(null);
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }

    @Test
    void givenBlankSender_whenValidate_thenReceiverIsNotComparedToSender() {
        Payment payment = PaymentTestData.validPayment();
        payment.setSenderAccount(" ");
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }
}
