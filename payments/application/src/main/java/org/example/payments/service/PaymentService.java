package org.example.payments.service;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.repository.PaymentRepository;
import org.example.useCases.CreatePaymentUseCase;
import org.springframework.stereotype.Service;
import org.example.model.*;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final PaymentRepository repository;

    public PaymentService(CreatePaymentUseCase createPaymentUseCase, PaymentRepository repository) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.repository = repository;
    }

    public Payment save(Payment payment) {
        return createPaymentUseCase.execute(payment);
    }

    public Optional<Payment> findBy(Long id) {
        return Optional.ofNullable(repository.findBy(id));
    }

    public List<Payment> findAll() {
        return repository.findAll();


    }
}
