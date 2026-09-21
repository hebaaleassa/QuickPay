package com.progressoft.quickpay.payments.resources;

import com.progressoft.quickpay.payments.domain.model.payment.BulkError;
import lombok.Data;

import java.util.List;

@Data
public class BulkUploadResultResponse {
    List<BulkError> failures;
    private int validRows;
    private int createdCount;
    private int failureCount;
}
