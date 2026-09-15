package org.example.payments.repository;

import org.example.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

        List<Payment> findBySenderAccount(String senderAccount);

}
