package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.repository.models.PaymentFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.PaymentSortField;

public interface PaymentSearchRepository {
    PagingResult<Payment> findAll(PaymentFilter paymentFilter,
                                  PagingOptions pagingOptions,
                                  PaymentSortField sortField,
                                  String direction);
}
