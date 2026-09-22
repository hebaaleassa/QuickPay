package org.example.validation;

import org.example.model.Payment;
import org.example.model.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidationTest {

    Payment payment =  new Payment();
    List<String> currencyValue = new ArrayList<>();

    @BeforeEach
    void setUp() {
        payment.setStatus("PENDING");
        payment.setSenderAccount("ACC12");
        payment.setReceiverAccount("ACC2");
        payment.setCreditorName("heba hasan");
        payment.setAmount(new BigDecimal("52.2"));

        currencyValue.add("USD");
        currencyValue.add("EUR");
    }

    @Test
    void givenValid_validateCurrency_thenTrue() {
        CurrencyValidation currencyValidation = new CurrencyValidation(currencyValue);
        payment.setCurrency("USD");
        List<Violation> validate = currencyValidation.validate(payment);
        assertTrue(validate.isEmpty());
    }

    @Test
    void givenInvalidLowerCase_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation(currencyValue);
        payment.setCurrency("usd");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency does not match", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

    @Test
    void givenNullCurrency_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation(currencyValue);
        payment.setCurrency(null);
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is null", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }
    @Test
    void givenEmpty_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation(currencyValue);
        payment.setCurrency("");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is blank", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

    @Test
    void givenBlankCurrency_validateCurrency_thenFalse() {
        CurrencyValidation currencyValidation = new CurrencyValidation(currencyValue);
        payment.setCurrency("   ");
        List<Violation> validate = currencyValidation.validate(payment);
        assertFalse(validate.isEmpty());
        assertEquals("currency is blank", validate.get(0).getMessage());
        assertEquals("currency", validate.get(0).getViolation());

    }

}