package com.progressoft.quickpay.payments.config;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public CreatePaymentUseCase createPaymentUseCase(
            PaymentRepository paymentRepository,
            @Qualifier("createPaymentValidators") ValidatorChain<Payment> validatorChain) {
        return new CreatePaymentUseCase(
                paymentRepository, validatorChain
        );
    }
}
