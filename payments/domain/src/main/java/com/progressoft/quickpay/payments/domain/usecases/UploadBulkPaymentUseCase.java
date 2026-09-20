package com.progressoft.quickpay.payments.domain.usecases;


import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.domain.ValidationError;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UploadBulkPaymentUseCase {

    private final ParseFileUseCase<Payment> parseFileUseCase;
    private final CreatePaymentUseCase createPaymentUseCase;

    public UploadBulkPaymentUseCase(ParseFileUseCase<Payment> parseFileUseCase, CreatePaymentUseCase createPaymentUseCase) {
        this.parseFileUseCase = parseFileUseCase;
        this.createPaymentUseCase = createPaymentUseCase;
    }

    public BulkUploadResult execute(String templateName, Path file) {
        ParseResult<Payment> result = parseFileUseCase.execute(new ParseFileUseCase.ParseFileCommand(templateName, file));

        List<String> failures = new ArrayList<>();
        Set<Integer> failedRows = new HashSet<>();
        for (ValidationError error : result.errors()) {
            failures.add(error.toString());
        }

        int created = 0;
        int failCount = failedRows.size();
        for (Payment payment : result.validRows()) {
            try {
                createPaymentUseCase.execute(payment);
                created++;
            } catch (SystemViolationException exception) {
                failCount++;
                String failureMessage = "payment from " + payment.getSenderAccount() + "to :" + payment.getReceiverAccount() + " failed :" + exception.getMessage();
                failures.add(failureMessage);
            }
        }

        return new BulkUploadResult(created, failCount, failures);
    }
}