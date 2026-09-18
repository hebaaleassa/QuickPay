package org.example.payments.config;


import org.example.model.Payment;
import org.example.repository.PaymentRepository;
import org.example.useCases.CreatePaymentUseCase;
import org.example.validation.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(@Qualifier("paymentValidatorChain") ValidatorChain<Payment> validatorChain
            ,PaymentRepository repository){

        return new CreatePaymentUseCase(validatorChain,repository);
    }
}
