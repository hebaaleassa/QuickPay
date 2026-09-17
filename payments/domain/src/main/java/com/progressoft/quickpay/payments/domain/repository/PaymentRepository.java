package com.progressoft.quickpay.payments.domain.repository;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);

    Optional<Payment> findBy(Long id);

    List<Payment> findAll();
}
