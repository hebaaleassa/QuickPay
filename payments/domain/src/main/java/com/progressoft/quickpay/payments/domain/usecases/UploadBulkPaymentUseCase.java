package com.progressoft.quickpay.payments.domain.usecases;


import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.*;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.validation.ValidationResult;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.domain.ValidationError;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UploadBulkPaymentUseCase {

    private final ParseFileUseCase<ParsedPayment> parseFileUseCase;
    private final ValidatorChain<Payment> validatorChain;
    private final PaymentRepository paymentRepository;
    private final boolean presistDBonError;

    public UploadBulkPaymentUseCase(ParseFileUseCase<ParsedPayment> parseFileUseCase,
                                    ValidatorChain<Payment> validatorChain,
                                    PaymentRepository paymentRepository, boolean presistDBonError) {
        this.parseFileUseCase = parseFileUseCase;
        this.validatorChain = validatorChain;
        this.paymentRepository = paymentRepository;
        this.presistDBonError = presistDBonError;
    }

    public BulkUploadResult execute(String templateName, Path file) {
        ParseResult<ParsedPayment> result = parseFileUseCase.execute(new ParseFileUseCase.ParseFileCommand(templateName, file));

        Map<Integer, BulkError> errors = new TreeMap<>();
        for (ValidationError error : result.errors()) {
            getBulkError(errors, error.rowNumber()).errors().add(String.format("%s: %s",
                    error.fieldName(), error.message()));
        }
        List<Payment> validPayment = new ArrayList<>();
        for (ParsedPayment parsedPayment : result.validRows()) {
            try {
                applyPayment(parsedPayment, validPayment);
            } catch (SystemViolationException exception) {
                getBulkError(errors, parsedPayment.rowNumber()).errors().addAll(exception.getViolationMessage());
            }
        }
        List<Payment> resultPayments = validPayment;
        if (errors.isEmpty() || presistDBonError) {
            resultPayments = paymentRepository.saveAll(validPayment);
        }
        return new BulkUploadResult(validPayment.size(), errors.size(), resultPayments, List.copyOf(errors.values()));
    }

    private BulkError getBulkError(Map<Integer, BulkError> errors, int error) {
        return errors.computeIfAbsent(error, row -> new BulkError(
                row, new ArrayList<>()));
    }

    private void applyPayment(ParsedPayment parsedPayment, List<Payment> validPayment) {
        Payment payment = parsedPayment.payment();
        validatorChain.validate(payment).throwExceptionIfViolated();

        payment.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.MICROS));
        payment.setStatus(PaymentStatus.PENDING);

        validPayment.add(payment);
    }
}
