package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.domain.exception.InvalidSortFieldException;
import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import com.progressoft.quickpay.payments.domain.usecases.UploadBulkPaymentUseCase;
import com.progressoft.quickpay.payments.paging.PagingOptions;
import com.progressoft.quickpay.payments.paging.PagingResult;
import com.progressoft.quickpay.payments.repository.PaymentSearchRepository;
import com.progressoft.quickpay.payments.sorting.PaymentSortField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final PaymentSearchRepository searchRepository;
    private final CreatePaymentUseCase createPaymentUseCase;
    private final UploadBulkPaymentUseCase uploadBulkPaymentUseCase;

    public PaymentService(PaymentRepository repository,
                          PaymentSearchRepository searchRepository,
                          CreatePaymentUseCase createPaymentUseCase,
                          UploadBulkPaymentUseCase uploadBulkPaymentUseCase) {
        this.repository = repository;
        this.searchRepository = searchRepository;
        this.createPaymentUseCase = createPaymentUseCase;
        this.uploadBulkPaymentUseCase = uploadBulkPaymentUseCase;
    }

    public BulkUploadResult uploadBulk(String templateName, Path file) {
        log.info("Uploading bulk payments");
        BulkUploadResult result = uploadBulkPaymentUseCase.execute(templateName, file);
        return result;
    }

    public void create(Payment payment) {
        log.info("Creating payment");
        createPaymentUseCase.execute(payment);
        log.info("Payment Created Successfully");
    }

    public List<Payment> findAll() {
        return repository.findAll();
    }

    public PagingResult<Payment> findAll(PaymentFilter filter, PagingOptions pagingOptions, String sortBy, String direction) {
        log.info("finding payments");
        PaymentSortField sortField = PaymentSortField.from(sortBy).orElseThrow(() -> new InvalidSortFieldException(sortBy));
        return searchRepository.findAll(filter, pagingOptions, sortField, direction);
    }

    public Optional<Payment> findBy(Long id) {
        return repository.findBy(id);
    }
}