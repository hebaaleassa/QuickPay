package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentService(PaymentRepository repository,
                          CreatePaymentUseCase createPaymentUseCase) {
        this.repository = repository;
        this.createPaymentUseCase = createPaymentUseCase;
    }

    public void create(Payment payment) {
        log.info("Creating payment");
        createPaymentUseCase.execute(payment);
        log.info("Payment Created Successfully");
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public Optional<Payment> findBy(Long id) {
        return repository.findBy(id);
    }
}