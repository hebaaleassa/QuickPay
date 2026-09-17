package org.example.payments.config;

import org.example.model.Payment;
import org.example.validation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ValidationConfig {

    @Bean
    public AmountValidation amountValidation() {
        return new AmountValidation();
    }

    @Bean
    public CreditorNameValidation creditorNameValidation() {
        return new CreditorNameValidation();
    }

    @Bean
    public CurrencyValidation currencyValidation() {
        return new CurrencyValidation();
    }

    @Bean
    public NotesValidation notesValidation() {
        return new NotesValidation();
    }

    @Bean
    public ReceiverAccountValidation receiverAccountValidation() {
        return new ReceiverAccountValidation();
    }

    @Bean
    public SenderAccountValidation senderAccountValidation() {
        return new SenderAccountValidation();
    }

    @Bean
    @Qualifier("paymentValidatorChain")
    public ValidatorChain<Payment> paymentValidatorChain(List<Validator<Payment>> validators) {
        return new ValidatorChain<>(validators);
    }
}
