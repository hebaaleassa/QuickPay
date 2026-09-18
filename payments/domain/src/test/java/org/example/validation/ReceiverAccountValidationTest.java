package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiverAccountValidationTest {

    ReceiverAccountValidation receiverAccountValidation = new ReceiverAccountValidation();
    Payment payment = new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setCreditorName("heba hasan");
        payment.setCurrency("USD");
        payment.setAmount(new BigDecimal("52.2"));

    }

    @Test
    void givenValidPayment_whenValidateReceiverAccount_thenTrue() {
        payment.setReceiverAccount("ACB12");
        assertTrue(receiverAccountValidation.validate(payment).isEmpty());
    }

    @Test
    void givenBlankReceiverAccount_whenValidateReceiverAccount_thenFalse() {
        payment.setReceiverAccount("   ");
        List<Violation> validate = receiverAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("receiver Account is blank", validate.get(0).getMessage());
        assertEquals("receiver Account", validate.get(0).getViolation());

    }

    @Test
    void givenEmptyReceiverAccount_whenValidateReceiverAccount_thenFalse() {
        payment.setReceiverAccount("");
        List<Violation> validate = receiverAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("receiver Account is blank", validate.get(0).getMessage());
        assertEquals("receiver Account", validate.get(0).getViolation());
    }

    @Test
    void givenNullReceiverAccount_whenValidateReceiverAccount_thenFalse() {
        payment.setReceiverAccount(null);
        List<Violation> validate = receiverAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("receiver Account is null", validate.get(0).getMessage());
        assertEquals("receiver Account", validate.get(0).getViolation());
    }

    @Test
    void givenSameSenderAccount_whenValidateReceiverAccount_thenFalse() {
        payment.setReceiverAccount(payment.getSenderAccount());
        List<Violation> validate = receiverAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("receiver Account is the same as sender Account", validate.get(0).getMessage());
        assertEquals("receiver Account", validate.get(0).getViolation());
    }

    @Test
    void givenTooLongReceiverAccount_whenValidateReceiverAccount_thenFalse() {
        String name = "A".repeat(35);
        payment.setReceiverAccount(name);
        List<Violation> validate = receiverAccountValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("receiver Account length is too long", validate.get(0).getMessage());
        assertEquals("receiver Account", validate.get(0).getViolation());
    }






}