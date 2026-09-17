package com.progressoft.quickpay.payments.entity;

import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Table(name = "payments")
@Setter
@Getter
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_sequence")
    @SequenceGenerator(
            name = "payment_sequence",
            sequenceName = "payments_sequence",
            allocationSize = 1
    )
    private Long id;

    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Instant createdAt;
    private String notes;
    private String creditorName;


}
