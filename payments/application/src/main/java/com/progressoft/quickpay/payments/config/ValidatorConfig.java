package com.progressoft.quickpay.payments.config;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.validation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public CurrencyValidator currencyValidator() {
        return new CurrencyValidator();
    }

    @Bean
    public NotesValidator notesValidator() {
        return new NotesValidator();
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
