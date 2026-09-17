package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NotesValidatorTest {
    private final NotesValidator validator = new NotesValidator();

    @Test
    void givenValidNotes_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNotesOver500Characters_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setNotes("T".repeat(5600));
        Assertions.assertTrue(validator.validate(payment).isPresent());
    }

    @Test
    void givenNullNotes_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setNotes(null);
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }
}