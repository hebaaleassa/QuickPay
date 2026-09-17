package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SenderAccountValidationTest {
    SenderAccountValidation senderAccountValidation = new SenderAccountValidation();

    Payment payment = new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setReceiverAccount("ACC2");
        payment.setCreditorName("heba hasan");
        payment.setCurrency("USD");
        payment.setAmount(new BigDecimal("52.2"));

    }


    @Test
    void givenValidPayment_whenValidateSenderAccount_thenTrue() {
        payment.setSenderAccount("ACB12");
        assertTrue(senderAccountValidation.validate(payment).isEmpty());
    }

    @Test
    void givenBlankSenderAccount_whenValidateSenderAccountValidation_thenFalse() {
        payment.setSenderAccount("   ");
        List<Violation> validate = senderAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Sender account is blank", validate.get(0).getMessage());
        assertEquals("Sender account", validate.get(0).getViolation());

    }

    @Test
    void givenEmptySenderAccountValidation_whenValidateSenderAccount_thenFalse() {
        payment.setSenderAccount("");
        List<Violation> validate = senderAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Sender account is blank", validate.get(0).getMessage());
        assertEquals("Sender account", validate.get(0).getViolation());
    }

    @Test
    void givenNullSenderAccountValidation_whenValidateSenderAccount_thenFalse() {
        payment.setReceiverAccount(null);
        List<Violation> validate = senderAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Sender account is null", validate.get(0).getMessage());
        assertEquals("Sender account", validate.get(0).getViolation());
    }


    @Test
    void givenTooLongSenderAccountValidation_whenValidateReceiverAccount_thenFalse() {
        String name = "A".repeat(35);
        payment.setSenderAccount(name);
        List<Violation> validate = senderAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Sender account length is too long, more than 34", validate.get(0).getMessage());
        assertEquals("Sender account", validate.get(0).getViolation());
    }


}