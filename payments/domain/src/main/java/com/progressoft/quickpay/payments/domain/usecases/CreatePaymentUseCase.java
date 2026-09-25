package com.progressoft.quickpay.payments.domain.usecases;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CreatePaymentUseCase implements UseCase<Payment> {
    private final PaymentRepository repository;
    private final ValidatorChain<Payment> validatorChain;

    public CreatePaymentUseCase(PaymentRepository repository, ValidatorChain<Payment> validatorChain) {
        this.repository = repository;
        this.validatorChain = validatorChain;
    }

    @Override
    public void execute(Payment payment) {
        validatorChain.validate(payment).throwExceptionIfViolated();
        payment.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.MICROS));
        payment.setStatus(PaymentStatus.PENDING);
        repository.save(payment);
    }
}
