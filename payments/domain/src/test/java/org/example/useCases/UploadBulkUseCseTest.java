package org.example.useCases;

import org.example.exception.SystemViolationException;
import org.example.model.BulkResult;
import org.example.model.Payment;

import org.example.model.Violation;
import org.example.validation.ValidatorChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.example.repository.PaymentRepository;
import org.example.validation.ValidationResult;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.nio.file.Path;

import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class UploadBulkUseCseTest {

    @Mock
    private ParseFileUseCase<Payment> parseFileUseCase;

    @Mock
    private ValidatorChain<Payment> validatorChain;

    @Mock
    private PaymentRepository paymentRepository;

    private UploadBulkUseCse useCase;


    ParseFileUseCase.ParseFileCommand command;

    @BeforeEach
    void setUp() {
        useCase = new UploadBulkUseCse(parseFileUseCase, validatorChain, paymentRepository, false);
        command = new ParseFileUseCase.ParseFileCommand("payment-default", Path.of("payments.csv"));
    }

    @Test
    void givenValidPayment_whenExecute_thenSavePayment() {

        Payment payment = new Payment();
        ValidationResult validationResult =
                mock(ValidationResult.class);

        when(parseFileUseCase.execute(any()))
                .thenReturn(new ParseResult<>(List.of(payment), List.of()));

        when(validatorChain.validate(payment))
                .thenReturn(validationResult);

        BulkResult result = useCase.execute(command);

        assertEquals(1, result.success());
        assertEquals(0, result.failure());

    }

    @Test
    void givenInvalidPayment_whenExecute_thenDoNotSavePayment() {

        Payment payment = new Payment();

        Violation violation = mock(Violation.class);
        when(violation.getViolation()).thenReturn("amount");
        when(violation.getMessage()).thenReturn("not positive");

        SystemViolationException exception = mock(SystemViolationException.class);

        when(exception.getViolations()).thenReturn(Set.of(violation));
        ValidationResult validationResult = mock(ValidationResult.class);

        doThrow(exception)
                .when(validationResult)
                .throwExceptionIfViolated();

        when(parseFileUseCase.execute(any()))
                .thenReturn(new ParseResult<>(List.of(payment), List.of()));

        when(validatorChain.validate(payment))
                .thenReturn(validationResult);

        BulkResult result = useCase.execute(command);

        assertEquals(0, result.success());
        assertEquals(1, result.failure());
    }

}