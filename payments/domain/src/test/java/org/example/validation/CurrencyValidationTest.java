package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidationTest {

    Payment payment =  new Payment();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setReceiverAccount("ACC2");
        payment.setCreditorName("heba hasan");
        payment.setAmount(new BigDecimal("52.2"));

    }

    @Test
    void givenValid_validateCurrency_thenTrue() {
        CurrencyValidation currencyValidation = new CurrencyValidation();
        payment.setCurrency("USD");
        List<Violation> validate = currencyValidation.validate(payment);
        assertTrue(validate.isEmpty());
    }

    @Test
    void givenInvalidLowerCase_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation();
        payment.setCurrency("usd");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency does not match the role of three uppercase letters", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

    @Test
    void givenNullCurrency_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation();
        payment.setCurrency(null);
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is null", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }
    @Test
    void givenEmpty_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation();
        payment.setCurrency("");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is blank", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

    @Test
    void givenBlankCurrency_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation();
        payment.setCurrency("   ");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is blank", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

}