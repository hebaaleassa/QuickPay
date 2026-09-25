package com.progressoft.quickpay.payments.domain.usecases;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.BulkError;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.ParsedPayment;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.validation.AmountValidator;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import com.progressoft.training.fileparser.domain.ParseResult;
import com.progressoft.training.fileparser.domain.ValidationError;
import com.progressoft.training.fileparser.usecase.ParseFileUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        return new UploadBulkPaymentUseCase(parseFileUseCase, validatorChain, paymentRepository, false);
    }

    @Test
    void givenValidPayment_whenExecute_thenPaymentIsSaved() {
        Payment payment = PaymentTestData.validPayment();
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(
                new ParseResult<>(List.of(new ParsedPayment(2, payment)), List.of()));

        BulkUploadResult result = useCase().execute("default", file);
        Assertions.assertEquals(1, result.successCount());
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

    // Decision table on saving: (row errors?, persist-db-on-error?) -> saveAll called?
    //   no errors,  persist=false -> saved      (givenValidPayment_whenExecute_thenPaymentIsSaved + next test)
    //   errors,     persist=false -> not saved  (givenRowErrors_andPersistOnErrorDisabled_...)
    //   errors,     persist=true  -> saved      (givenRowErrors_andPersistOnErrorEnabled_...)

    @Test
    void givenValidPayment_whenExecute_thenSavedPaymentsAreReturnedAsPending() {
        Payment payment = PaymentTestData.validPayment();
        Payment saved = PaymentTestData.validPayment();
        saved.setId(1L);
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(
                new ParseResult<>(List.of(new ParsedPayment(2, payment)), List.of()));
        Mockito.when(paymentRepository.saveAll(List.of(payment))).thenReturn(List.of(saved));

        BulkUploadResult result = useCase().execute("default", file);

        Assertions.assertEquals(List.of(saved), result.validRows());
        Assertions.assertEquals(PaymentStatus.PENDING, payment.getStatus());
        Assertions.assertNotNull(payment.getCreatedAt());
    }

    @Test
    void givenTemplateNameAndFile_whenExecute_thenBothArePassedToTheParser() {
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(new ParseResult<>(List.of(), List.of()));
        ArgumentCaptor<ParseFileUseCase.ParseFileCommand> command = ArgumentCaptor.forClass(ParseFileUseCase.ParseFileCommand.class);

        useCase().execute("custom", file);

        Mockito.verify(parseFileUseCase).execute(command.capture());
        Assertions.assertEquals("custom", command.getValue().templateName());
        Assertions.assertSame(file, command.getValue().file());
    }

    @Test
    void givenParserErrors_whenExecute_thenErrorsAreGroupedByRowAndSortedByRowNumber() {
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(new ParseResult<>(List.of(), List.of(
                new ValidationError(5, "amount", "is required but was empty"),
                new ValidationError(3, "currency", "is 4 characters, maximum is 3"),
                new ValidationError(5, "notes", "is 501 characters, maximum is 500"))));

        BulkUploadResult result = useCase().execute("default", file);

        Assertions.assertEquals(2, result.failureCount());
        Assertions.assertEquals(List.of(
                new BulkError(3, List.of("currency: is 4 characters, maximum is 3")),
                new BulkError(5, List.of("amount: is required but was empty", "notes: is 501 characters, maximum is 500"))),
                result.failures());
    }

    @Test
    void givenRowErrors_andPersistOnErrorDisabled_whenExecute_thenNothingSavedButValidRowsReported() {
        Payment valid = PaymentTestData.validPayment();
        Payment invalid = PaymentTestData.invalidAmountPayment();
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(new ParseResult<>(
                List.of(new ParsedPayment(2, valid), new ParsedPayment(3, invalid)), List.of()));

        BulkUploadResult result = useCase().execute("default", file);

        Mockito.verify(paymentRepository, Mockito.never()).saveAll(Mockito.any());
        Assertions.assertEquals(1, result.successCount());
        Assertions.assertEquals(1, result.failureCount());
        Assertions.assertEquals(List.of(valid), result.validRows());
        Assertions.assertEquals(List.of(new BulkError(3, List.of("[amount] amount must be greater than zero"))), result.failures());
    }

    @Test
    void givenRowErrors_andPersistOnErrorEnabled_whenExecute_thenValidRowsAreSaved() {
        Payment valid = PaymentTestData.validPayment();
        Payment saved = PaymentTestData.validPayment();
        saved.setId(1L);
        Mockito.when(parseFileUseCase.execute(Mockito.any())).thenReturn(new ParseResult<>(
                List.of(new ParsedPayment(2, valid)), List.of(new ValidationError(3, "All", "Expected 6 fields found 4"))));
        Mockito.when(paymentRepository.saveAll(List.of(valid))).thenReturn(List.of(saved));
        ValidatorChain<Payment> validatorChain = new ValidatorChain<>(List.of(new AmountValidator()));
        UploadBulkPaymentUseCase useCase = new UploadBulkPaymentUseCase(parseFileUseCase, validatorChain, paymentRepository, true);

        BulkUploadResult result = useCase.execute("default", file);

        Assertions.assertEquals(1, result.successCount());
        Assertions.assertEquals(1, result.failureCount());
        Assertions.assertEquals(List.of(saved), result.validRows());
    }
}
