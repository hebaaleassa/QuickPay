package org.example.repository;

import org.example.model.PageResult;
import org.example.model.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    List<Payment> findAll();
    PageResult<Payment> findAll(int page, int size);
    Payment findBy(Long id);
    List<Payment> saveAll(List<Payment> payments);
}
