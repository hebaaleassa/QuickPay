package com.progressoft.quickpay.payments.resources;

import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
public class PaymentResponse {
    private Long id;

    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private Instant createdAt;
    private String notes;
    private String creditorName;

}
