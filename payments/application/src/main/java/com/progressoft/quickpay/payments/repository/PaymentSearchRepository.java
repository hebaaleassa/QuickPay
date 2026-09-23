package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.paging.PagingOptions;
import com.progressoft.quickpay.payments.paging.PagingResult;
import com.progressoft.quickpay.payments.sorting.PaymentSortField;

public interface PaymentSearchRepository {
    PagingResult<Payment> findAll(PaymentFilter paymentFilter,
                                  PagingOptions pagingOptions,
                                  PaymentSortField sortField,
                                  String direction);
}
