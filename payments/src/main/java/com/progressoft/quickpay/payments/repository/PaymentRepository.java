package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
