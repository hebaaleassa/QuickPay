package com.progressoft.quickpay.payments.domain;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;

import java.math.BigDecimal;

public class PaymentTestData {
    public static Payment validPayment() {
        Payment payment = new Payment();
        payment.setSenderAccount("ACC-1001");
        payment.setReceiverAccount("ACC-2002");
        payment.setAmount(new BigDecimal("250.75"));
        payment.setCurrency("USD");
        payment.setNotes("rent");
        payment.setCreditorName("Tolay Khamis");
        return payment;
    }

    public static Payment invalidPayment() {
        Payment payment = validPayment();
        payment.setAmount(new BigDecimal("-50"));
        return payment;
    }
}