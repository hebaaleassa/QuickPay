package org.example.payments.service;

import org.example.payments.model.Payment;
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

    public Payment SavePayment(Payment payment) {
        return repository.save(payment);
    }

    public Optional<Payment> findPaymentById(Long id) {
        return repository.findById(id);
    }

    public List<Payment> findAllPayment() {
        return repository.findAll();
    }


    public List<Payment> findAllBySender(String senderAccount) {
        return repository.findBySenderAccount(senderAccount);
    }


}
