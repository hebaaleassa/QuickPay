package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenReceiverSameAsSender_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("ACC-1");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenReceiverSameAsSenderLowerCase_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setReceiverAccount("acc-1");
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }
}