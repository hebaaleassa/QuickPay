package org.example.payments.resource;

import lombok.*;
import org.example.model.BulkErrors;
import org.example.model.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class BulkUploadResponse {
    private int total;
    private int success;
    private int failure;
    Map<Integer, List<String>> rowErrors;
    private List<PaymentResponse> payments;
}
