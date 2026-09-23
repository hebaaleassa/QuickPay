package com.progressoft.quickpay.payments.repository.models;

import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentFilter(Long id, String senderAccount, String receiverAccount,
                            String currency, PaymentStatus status, String notes,
                            String creditorName, Instant createdAt,
                            BigDecimal maxAmount, BigDecimal minAmount) {
}
