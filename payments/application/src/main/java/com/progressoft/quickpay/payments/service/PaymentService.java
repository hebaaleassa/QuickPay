package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import com.progressoft.quickpay.payments.domain.usecases.UploadBulkPaymentUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final CreatePaymentUseCase createPaymentUseCase;
    private final UploadBulkPaymentUseCase uploadBulkPaymentUseCase;

    public PaymentService(PaymentRepository repository,
                          CreatePaymentUseCase createPaymentUseCase, UploadBulkPaymentUseCase uploadBulkPaymentUseCase) {
        this.repository = repository;
        this.createPaymentUseCase = createPaymentUseCase;
        this.uploadBulkPaymentUseCase = uploadBulkPaymentUseCase;
    }

    public BulkUploadResult uploadBulk(String templateName, Path file) {
        log.info("Uploading bulk payments");
        return uploadBulkPaymentUseCase.execute(templateName, file);
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