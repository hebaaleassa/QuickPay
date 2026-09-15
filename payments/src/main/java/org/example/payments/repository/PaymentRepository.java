package org.example.payments.repository;

import org.example.payments.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

        List<PaymentEntity> findBySenderAccount(String senderAccount);

}
