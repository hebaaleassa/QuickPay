package com.progressoft.quickpay.payments.resources.sorting;

import java.util.Arrays;
import java.util.Optional;

public enum TemplateSortField {

    ID("id"),
    NAME("name");

    private final String fieldName;

    TemplateSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Optional<TemplateSortField> from(String fieldName) {
        return Arrays.stream(values()).filter(field -> field.fieldName.equalsIgnoreCase(fieldName)).findFirst();
    }

    public String fieldName() {
        return fieldName;
    }
}