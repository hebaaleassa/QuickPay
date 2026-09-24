package com.progressoft.quickpay.payments;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;

public class PaymentTestData {
    public static final Long PAYMENT_ID = 100L;

    public static Payment validPayment() {
        Payment payment = new Payment();
        payment.setSenderAccount("ACC-1");
        payment.setReceiverAccount("ACC-2");
        payment.setAmount(new BigDecimal("25000.75"));
        payment.setCurrency("JOD");
        payment.setNotes("payment");
        payment.setCreditorName("Tolay Khamis");
        return payment;
    }

    public static Payment savedPayment(Long id) {
        Payment payment = validPayment();
        payment.setId(id);
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }

    public static Payment paymentWithNegativeAmount() {
        Payment payment = validPayment();
        payment.setAmount(new BigDecimal("-50"));
        return payment;
    }

    public static PaymentEntity newEntity() {
        PaymentEntity entity = new PaymentEntity();
        entity.setSenderAccount("ACC-1");
        entity.setReceiverAccount("ACC-2");
        entity.setAmount(new BigDecimal("25000.75"));
        entity.setCurrency("JOD");
        entity.setStatus(PaymentStatus.PENDING);
        entity.setNotes("payment");
        entity.setCreditorName("Tolay Khamis");
        return entity;
    }

    public static PaymentEntity savedEntity(Long id) {
        PaymentEntity entity = newEntity();
        entity.setId(id);
        return entity;
    }

    public static String validJson() {
        return """
                {
                "senderAccount": "ACC-1",
                "receiverAccount": "ACC-2",
                "amount": 25000.75,
                "currency": "JOD",
                "notes": "payment",
                "creditorName": "Tolay Khamis"
                }""";
    }

    public static MockMultipartFile bulkFile() {
        return new MockMultipartFile("file", "payments.csv",
                "text/csv", "test data".getBytes());
    }

    public static MockMultipartFile metadataFile() {
        return new MockMultipartFile(
                "metadata",
                "",
                "application/json", """
                {
                    "templateName": "default"
                }""".getBytes());
    }
}