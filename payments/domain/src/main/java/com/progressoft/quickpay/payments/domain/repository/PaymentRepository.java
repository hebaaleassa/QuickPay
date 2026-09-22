package com.progressoft.quickpay.payments.domain.repository;

import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);

    List<Payment> saveAll(List<Payment> payments);

    Optional<Payment> findBy(Long id);

    List<Payment> findAll();

    List<Payment> findAll(String sortBy, String direction);

    List<Payment> findAll(PaymentFilter paymentFilter);

    PagingResult<Payment> findAll(PagingOptions pagingOptions);
}
