package com.progressoft.quickpay.payments.domain.model.payment;

import java.util.List;

public record BulkError(int rowNumber, List<String> errors) {
}
