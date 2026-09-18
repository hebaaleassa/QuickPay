package org.example.repository;

import org.example.model.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    List<Payment> findAll();
    Payment findBy(Long id);
}
