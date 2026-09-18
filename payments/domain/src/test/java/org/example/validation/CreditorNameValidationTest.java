package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreditorNameValidationTest {

    private CreditorNameValidation validator;
    Payment payment = new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setReceiverAccount("ACC2");
        payment.setCurrency("USD");
        payment.setAmount(new BigDecimal("52.2"));

    }

    @Test
    void givenValidInput_whenValidateCreditorName_thenTrue() {
        validator = new CreditorNameValidation();
        payment.setCreditorName("heba hasan");
        List<Violation> validate = validator.validate(payment);
        assertTrue(validate.isEmpty());
    }

    @Test
    void givenInvalidInput_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        payment.setCreditorName("heba");
        List<Violation> validate = validator.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("creditor Name must be two parts", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }

    @Test
    void givenBlankName_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        payment.setCreditorName("   ");
        List<Violation> validate = validator.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("creditor Name is blank", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }

    @Test
    void givenEmptyName_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        payment.setCreditorName("");
        List<Violation> validate = validator.validate(payment);
        assertFalse(validator.validate(payment).isEmpty());
        assertEquals("creditor Name is blank", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }

    @Test
    void givenNullName_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        payment.setCreditorName(null);
        List<Violation> validate = validator.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("creditor Name is null", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }

    @Test
    void givenTooLongName_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        String name = "A".repeat(50) + " " + "B".repeat(55);
        payment.setCreditorName(name);
        List<Violation> validate = validator.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("creditor Name length is too long, more than 100", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }

    @Test
    void givenNonAlphanumeric_whenValidateCreditorName_thenFalse() {
        validator = new CreditorNameValidation();
        payment.setCreditorName("heba hasan$");
        List<Violation> validate = validator.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("creditor Name must be alphanumeric only", validate.get(0).getMessage());
        assertEquals("creditor Name", validate.get(0).getViolation());

    }




}

