package com.progressoft.quickpay.payments.domain;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;

import java.math.BigDecimal;

public class PaymentTestData {
    public static Payment validPayment() {
        Payment payment = new Payment();
        payment.setSenderAccount("ACC-1");
        payment.setReceiverAccount("ACC-2");
        payment.setAmount(new BigDecimal("27000.75"));
        payment.setCurrency("JOD");
        payment.setNotes("payment");
        payment.setCreditorName("Tolay Khamis");
        return payment;
    }

    public static Payment invalidAmountPayment() {
        Payment payment = validPayment();
        payment.setAmount(new BigDecimal("-10"));
        return payment;
    }
}