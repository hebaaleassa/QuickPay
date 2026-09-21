package com.progressoft.quickpay.payments.domain.model.payment;

import java.util.List;

public record BulkUploadResult(int createdCount, int validRows, int failureCount, List<Payment> created,
                               List<BulkError> failures) {
}
