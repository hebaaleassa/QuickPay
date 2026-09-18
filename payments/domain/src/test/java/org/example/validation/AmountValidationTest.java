package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmountValidationTest {

    Payment payment = new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setReceiverAccount("ACC2");
        payment.setCurrency("USD");
        payment.setCreditorName("heba hasan");
    }

    @Test
    void givenValidInput_whenValidateAmount_returnsTrue() {
        AmountValidation validation = new AmountValidation();
        payment.setAmount(new BigDecimal("25.0"));
        List<Violation> validate = validation.validate(payment);

        assertTrue(validate.isEmpty());
    }

    @Test
    void givenNegativeInput_whenValidateAmount_returnsFalse() {
        AmountValidation validation = new AmountValidation();
        payment.setAmount(new BigDecimal("-2500.0"));
        List<Violation> validate = validation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Amount is not positive", validate.get(0).getMessage());
        assertEquals("amount", validate.get(0).getViolation());
    }

    @Test
    void givenInValidInput_whenValidateAmount_returnsFalse() {

        AmountValidation validation = new AmountValidation();
        payment.setAmount(BigDecimal.valueOf(0));
        List<Violation> validate = validation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Amount is not positive", validate.get(0).getMessage());
        assertEquals("amount", validate.get(0).getViolation());

    }

    @Test
    void givenMoreTwoDecimal_whenValidateAmount_returnsFalse() {

        AmountValidation validation = new AmountValidation();
        payment.setAmount(new BigDecimal("44.995"));
        List<Violation> validate = validation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Amount scale is more than two", validate.get(0).getMessage());
        assertEquals("amount", validate.get(0).getViolation());

    }

    @Test
    void givenNull_whenValidateAmount_returnsFalse() {

        AmountValidation validation = new AmountValidation();
        payment.setAmount(null);
        List<Violation> validate = validation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("Amount is null", validate.get(0).getMessage());
        assertEquals("amount", validate.get(0).getViolation());

    }
}
