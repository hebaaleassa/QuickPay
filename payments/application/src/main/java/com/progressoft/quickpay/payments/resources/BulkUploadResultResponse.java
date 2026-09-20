package com.progressoft.quickpay.payments.resources;

import lombok.Data;

import java.util.List;

@Data
public class BulkUploadResultResponse {
    List<String> failures;
    private int createdCount;
    private int failureCount;
}
