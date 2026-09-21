package com.progressoft.quickpay.payments.resources;

import com.progressoft.quickpay.payments.domain.model.payment.BulkError;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import lombok.Data;

import java.util.List;

@Data
public class BulkUploadResultResponse {
    List<BulkError> failures;
    List<Payment> validRows;
    private int successCount;
    private int failureCount;
}
