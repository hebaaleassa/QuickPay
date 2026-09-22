package org.example.useCases;

import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.domain.ValidationError;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;
import org.example.exception.SystemViolationException;
import org.example.model.BulkErrors;
import org.example.model.BulkResult;
import org.example.model.Payment;
import org.example.model.Violation;
import org.example.repository.PaymentRepository;
import org.example.validation.ValidationResult;
import org.example.validation.ValidatorChain;

import java.time.Instant;
import java.util.*;

public class UploadBulkUseCse implements UseCase<ParseFileUseCase.ParseFileCommand, BulkResult> {

    private final ParseFileUseCase<Payment> parseFileUseCase;
    private final ValidatorChain<Payment> validatorChain;
    private final PaymentRepository paymentRepository;
    private final Boolean saveIfError;

    public UploadBulkUseCse(ParseFileUseCase<Payment> parseFileUseCase,
                            ValidatorChain<Payment> validatorChain,
                            PaymentRepository paymentRepository, boolean saveIfError) {
        this.parseFileUseCase = parseFileUseCase;
        this.validatorChain = validatorChain;
        this.paymentRepository = paymentRepository;
        this.saveIfError = saveIfError;
    }

    @Override
    public BulkResult execute(ParseFileUseCase.ParseFileCommand parseFileCommand) {
        ParseResult<Payment> result = parseFileUseCase.execute(parseFileCommand);

        List<Payment> payments = new ArrayList<>();
        Map<Integer, List<String>> allErrors = new TreeMap<>();

        addErrors(result, allErrors);
        createPayment(result, payments, allErrors);
        int failer = allErrors.size();
        int total = payments.size() + failer;

        if (saveIfError) {
            payments = paymentRepository.saveAll(payments);
        }
        return new BulkResult(total, payments.size(), failer, allErrors, payments);
    }

    private void addErrors(ParseResult<Payment> result,
                           Map<Integer, List<String>> allErrors) {
        for (ValidationError validationError : result.errors()) {

            allErrors.computeIfAbsent(validationError.rowNumber(),
                    k -> new ArrayList<>()).add(validationError.fieldName() + ": " + validationError.message());
        }
    }

    private void createPayment(ParseResult<Payment> result
            , List<Payment> payments, Map<Integer, List<String>> allErrors) {
        int rowNumber = 2;

        for (Payment row : result.validRows()) {
            try {
                ValidationResult validationResult = validatorChain.validate(row);
                validationResult.throwExceptionIfViolated();
                row.setCreatedAt(Instant.now());
                row.setStatus("PENDING");
                payments.add(row);

            } catch (SystemViolationException exception) {

                for (Violation violation : exception.getViolations()) {
                    allErrors.computeIfAbsent(rowNumber,
                            k -> new ArrayList<>()).add(violation.getViolation() + ": " + violation.getMessage());
                }
            }
            rowNumber++;
        }
    }
}
