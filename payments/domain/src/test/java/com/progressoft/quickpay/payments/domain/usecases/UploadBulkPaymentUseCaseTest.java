package com.progressoft.quickpay.payments.domain.usecases;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.ParsedPayment;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.validation.AmountValidator;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UploadBulkPaymentUseCaseTest {

    @Mock
    private ParseFileUseCase<ParsedPayment> parseFileUseCase;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private Path file;

    private UploadBulkPaymentUseCase useCase() {
        ValidatorChain<Payment> validatorChain = new ValidatorChain<>(List.of(new AmountValidator()));
        return new UploadBulkPaymentUseCase(parseFileUseCase, validatorChain);
    }

    @Test
    void givenValidPayment_whenExecute_thenPaymentIsSaved() {
        Payment payment = PaymentTestData.validPayment();
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(
                new ParseResult<>(List.of(new ParsedPayment(2, payment)), List.of()));

        BulkUploadResult result = useCase().execute("default", file);
        Assertions.assertEquals(1, result.createdCount());
        Assertions.assertEquals(0, result.failureCount());
    }

    @Test
    void givenInvalidPayment_whenExecute_thenPaymentFails() {
        Payment payment = PaymentTestData.invalidAmountPayment();
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(
                new ParseResult<>(List.of(new ParsedPayment(2, payment)), List.of()));

        BulkUploadResult result = useCase().execute("default", file);

        Assertions.assertEquals(1, result.failureCount());
        Mockito.verify(paymentRepository, Mockito.never()).save(payment);
    }
}