package org.example.useCases;

import org.example.model.Payment;
import org.example.repository.PaymentRepository;
import org.example.validation.ValidationResult;
import org.example.validation.ValidatorChain;

import java.time.Instant;

public class CreatePaymentUseCase implements UseCase<Payment, Payment> {
    private final ValidatorChain<Payment> validatorChain;
    private final PaymentRepository repository;

    public CreatePaymentUseCase(ValidatorChain<Payment> validatorChain, PaymentRepository repository) {
        this.validatorChain = validatorChain;
        this.repository = repository;
    }

    @Override
    public Payment execute(Payment input) {
        ValidationResult result = validatorChain.validate(input);
        result.throwExceptionIfViolated();
        input.setCreatedAt(Instant.now());
        input.setStatus("PENDING");
        return repository.save(input);
    }
}
