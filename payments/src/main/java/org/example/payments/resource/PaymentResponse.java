package org.example.payments.resource;

import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PaymentResponse {
    private Long id;
    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private String status;
    private Instant createdAt;
    private String notes;
}

