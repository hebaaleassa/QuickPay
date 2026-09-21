package com.progressoft.quickpay.payments.resources;

import lombok.Data;

import java.util.List;

@Data
public class BulkUploadResultResponse {
    List<BulkErrorResponse> failures;
    List<PaymentResponse> validRows;
    private int successCount;
    private int failureCount;
}
