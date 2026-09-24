package com.progressoft.quickpay.payments.domain.validation;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.violation.Violation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class NotesValidatorTest {
    private final NotesValidator validator = new NotesValidator(500);

    @Test
    void givenValidNotes_whenValidate_thenNoViolation() {
        Assertions.assertTrue(validator.validate(PaymentTestData.validPayment()).isEmpty());
    }

    @Test
    void givenNotesOver500Characters_whenValidate_thenViolationReturned() {
        Payment payment = PaymentTestData.validPayment();
        payment.setNotes("T".repeat(5600));
        List<Violation> violations = validator.validate(payment);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("notes length should be less than 500", violations.get(0).message());
        Assertions.assertEquals("NotesValidator", violations.get(0).violator());
    }

    @Test
    void givenNullNotes_whenValidate_thenNoViolation() {
        Payment payment = PaymentTestData.validPayment();
        payment.setNotes(null);
        Assertions.assertTrue(validator.validate(payment).isEmpty());
    }
}