package com.progressoft.quickpay.payments.resources;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private String notes;
    private String creditorName;
}
