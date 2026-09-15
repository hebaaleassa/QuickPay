package org.example.payments.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private String status;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    private String notes;

    @PrePersist
    protected void onCreate(){
        this.createdAt = Instant.now();
    }
}
