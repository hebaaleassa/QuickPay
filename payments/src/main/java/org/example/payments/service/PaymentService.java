package org.example.payments.service;

import org.example.payments.model.PaymentEntity;
import org.example.payments.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentEntity save(PaymentEntity payment) {
        return repository.save(payment);
    }

    public Optional<PaymentEntity> findBy(Long id) {
        return repository.findById(id);
    }

    public List<PaymentEntity> findAll() {
        return repository.findAll();
    }

    public List<PaymentEntity> findAllBy(String senderAccount) {
        return repository.findBySenderAccount(senderAccount);
    }
}
