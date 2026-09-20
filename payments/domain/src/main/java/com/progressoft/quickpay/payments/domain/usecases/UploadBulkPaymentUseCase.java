package com.progressoft.quickpay.payments.domain.usecases;


import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.BulkError;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.ParsedPayment;
import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.domain.ValidationError;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UploadBulkPaymentUseCase {

    private final ParseFileUseCase<ParsedPayment> parseFileUseCase;
    private final CreatePaymentUseCase createPaymentUseCase;

    public UploadBulkPaymentUseCase(ParseFileUseCase<ParsedPayment> parseFileUseCase, CreatePaymentUseCase createPaymentUseCase) {
        this.parseFileUseCase = parseFileUseCase;
        this.createPaymentUseCase = createPaymentUseCase;
    }

    public BulkUploadResult execute(String templateName, Path file) {
        ParseResult<ParsedPayment> result = parseFileUseCase.execute(new ParseFileUseCase.ParseFileCommand(templateName, file));

        Map<Integer, BulkError> errors = new TreeMap<>();
        for (ValidationError error : result.errors()) {
            errors.computeIfAbsent(error.rowNumber(), row -> new BulkError(
                    row, new ArrayList<>())).errors().add(String.format("%s: %s",
                    error.fieldName(), error.message()));
        }
        int created = 0;
        for (ParsedPayment payment : result.validRows()) {
            try {
                createPaymentUseCase.execute(payment.payment());
                created++;
            } catch (SystemViolationException exception) {
                errors.computeIfAbsent(payment.rowNumber(), row -> new BulkError(
                        row, new ArrayList<>())).errors().addAll(exception.getViolationMessage());
            }
        }
        return new BulkUploadResult(created, errors.size(), List.copyOf(errors.values()));
    }
}