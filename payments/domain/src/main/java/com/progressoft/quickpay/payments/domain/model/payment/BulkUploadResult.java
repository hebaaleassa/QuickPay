package com.progressoft.quickpay.payments.domain.model.payment;

import java.util.List;

public record BulkUploadResult(int successCount, int failureCount, List<Payment> validRows,
                               List<BulkError> failures) {
}
