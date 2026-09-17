package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotesValidationTest {

    NotesValidation notesValidation = new NotesValidation();
    Payment payment =  new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setReceiverAccount("ACC2");
        payment.setCreditorName("heba hasan");
        payment.setCurrency("USD");
        payment.setAmount(new BigDecimal("52.2"));


    }

    @Test
    void givenValid_validateNotes_thenTrue() {
        payment.setNotes("balance now is 522");
        assertTrue(notesValidation.validate(payment).isEmpty());
    }

    @Test
    void givenValid_validateNotes_thenFalse() {
        String notes = "A".repeat(555);
        payment.setNotes(notes);
        List<Violation> validate = notesValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("the length is longer than 500", validate.get(0).getMessage());
        assertEquals("notes", validate.get(0).getViolation());

    }

}