package com.progressoft.quickpay.payments.resources;

import lombok.Data;

import java.util.List;

@Data
public class BulkErrorResponse {
    private int rowNumber;
    private List<String> errors;
}
