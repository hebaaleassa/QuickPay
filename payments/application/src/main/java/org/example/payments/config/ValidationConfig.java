package org.example.payments.config;

import org.example.model.Payment;
import org.example.validation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    public CurrencyValidation currencyValidation(@Value("${payments.currency.default-currency:}") List<String> currencyValues ) {
        return new CurrencyValidation(currencyValues);
    }

    @Bean
    public NotesValidation notesValidation(@Value("${payments.notes.default-length:}") Integer maxLength) {
        return new NotesValidation(maxLength);
    }

    @Bean
    public ReceiverAccountValidation receiverAccountValidation() {
        return new ReceiverAccountValidation();
    }

    @Bean
    public SenderAccountValidation senderAccountValidation() {
        return new SenderAccountValidation();
    }


    @Bean("paymentValidatorChain")
    public ValidatorChain<Payment> paymentValidatorChain(
            SenderAccountValidation senderAccountValidation,
            ReceiverAccountValidation receiverAccountValidation,
            NotesValidation notesValidation,
            CurrencyValidation currencyValidation,
            CreditorNameValidation creditorNameValidation,
            AmountValidation amountValidation
    ) {
        return new ValidatorChain<>(
                List.of(
                        amountValidation,
                        creditorNameValidation,
                        currencyValidation,
                        notesValidation,
                        receiverAccountValidation,
                        senderAccountValidation
                )
        );
    }
}
