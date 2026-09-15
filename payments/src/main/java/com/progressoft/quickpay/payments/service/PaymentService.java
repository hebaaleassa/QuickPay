package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentEntity create(PaymentEntity payment) {
        payment.setCreatedAt(Instant.now());
        payment.setStatus("PENDING");
        return repository.save(payment);
    }

    public List<PaymentEntity> findAll() {
        return repository.findAll();
    }

    public Optional<PaymentEntity> findBy(Long id) {
        return repository.findById(id);
    }
}
