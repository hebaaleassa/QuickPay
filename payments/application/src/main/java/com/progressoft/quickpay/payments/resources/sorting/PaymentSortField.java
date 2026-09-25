package com.progressoft.quickpay.payments.resources.sorting;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentSortField {

    ID("id"),
    SENDER_ACCOUNT("senderAccount"),
    RECEIVER_ACCOUNT("receiverAccount"),
    AMOUNT("amount"),
    CURRENCY("currency"),
    NOTES("notes"),
    CREDITOR_NAME("creditorName"),
    STATUS("status"),
    CREATED_AT("createdAt");

    private final String fieldName;

    PaymentSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Optional<PaymentSortField> from(String fieldName) {
        return Arrays.stream(values()).filter(field -> field.fieldName.equalsIgnoreCase(fieldName)).findFirst();
    }

    public String fieldName() {
        return fieldName;
    }
}