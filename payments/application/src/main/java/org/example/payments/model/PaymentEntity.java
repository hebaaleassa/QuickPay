package org.example.payments.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private String status;
    private Instant createdAt;
    private String notes;
    private String creditorName;

}
