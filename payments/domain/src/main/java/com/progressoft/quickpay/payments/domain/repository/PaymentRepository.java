package com.progressoft.quickpay.payments.domain.repository;

import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;
import com.progressoft.quickpay.payments.domain.sorting.PaymentSortField;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);

    List<Payment> saveAll(List<Payment> payments);

    Optional<Payment> findBy(Long id);

    List<Payment> findAll();

    PagingResult<Payment> findAll(PaymentFilter paymentFilter, PagingOptions pagingOptions,
                                  PaymentSortField paymentSortField, String direction);
}
