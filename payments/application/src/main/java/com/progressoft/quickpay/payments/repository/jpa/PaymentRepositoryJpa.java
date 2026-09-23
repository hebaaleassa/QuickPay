package com.progressoft.quickpay.payments.repository.jpa;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepositoryJpa extends JpaRepository<PaymentEntity, Long>,
        JpaSpecificationExecutor<PaymentEntity> {
}
