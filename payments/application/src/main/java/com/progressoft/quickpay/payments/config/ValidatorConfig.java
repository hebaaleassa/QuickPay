package com.progressoft.quickpay.payments.config;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.validation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ValidatorConfig {

    @Bean
    public SenderAccountValidator senderAccountValidator() {
        return new SenderAccountValidator();
    }

    @Bean
    public RecieverAccountValidator recieverAccountValidator() {
        return new RecieverAccountValidator();
    }

    @Bean
    public AmountValidator amountValidator() {
        return new AmountValidator();
    }

    @Bean
    public CurrencyValidator currencyValidator(@Value("${payments.allowed-currencies}") String allowedCurrencies) {
        List<String> currency = Arrays.stream(allowedCurrencies.split(",")).map(String::trim).toList();
        return new CurrencyValidator(currency);
    }

    @Bean
    public NotesValidator notesValidator(@Value("${payments.notes-max-length}") int maxLength) {
        return new NotesValidator(maxLength);
    }

    @Bean
    public CreditorNameValidator creditorNameValidator() {
        return new CreditorNameValidator();
    }

    @Bean("createPaymentValidators")
    public ValidatorChain<Payment> createPaymentValidators(
            SenderAccountValidator senderAccountValidator,
            RecieverAccountValidator recieverAccountValidator,
            AmountValidator amountValidator,
            CurrencyValidator currencyValidator,
            CreditorNameValidator creditorNameValidator,
            NotesValidator notesValidator) {
        return new ValidatorChain<>(
                List.of(
                        senderAccountValidator,
                        recieverAccountValidator,
                        amountValidator,
                        currencyValidator,
                        creditorNameValidator,
                        notesValidator
                )
        );
    }
}
