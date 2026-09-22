package com.progressoft.quickpay.payments.domain.filteration;

import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;

import java.math.BigDecimal;

public record PaymentFilter(String currency, PaymentStatus status,
                            BigDecimal maxAmount, BigDecimal minAmount) {
}
