package com.progressoft.quickpay.payments.domain.model.payment;

import java.util.List;

public record BulkUploadResult(int createdCount, int failureCount, List<BulkError> failures) {
}
