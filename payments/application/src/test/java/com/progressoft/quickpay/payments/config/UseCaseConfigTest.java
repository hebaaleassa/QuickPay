package com.progressoft.quickpay.payments.config;

import com.progressoft.quickpay.payments.PaymentTestData;
import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UseCaseConfigTest {

    @Mock
    private PaymentRepository paymentRepository;

    private CreatePaymentUseCase useCase() {
        ValidatorConfig validatorConfig = new ValidatorConfig();
        return new UseCaseConfig().createPaymentUseCase(
                paymentRepository,
                validatorConfig.createPaymentValidators(List.of(
                        validatorConfig.senderAccountValidator(),
                        validatorConfig.recieverAccountValidator(),
                        validatorConfig.amountValidator(),
                        validatorConfig.currencyValidator(),
                        validatorConfig.notesValidator(),
                        validatorConfig.creditorNameValidator())));
    }

    @Test
    void givenValidPayment_whenExecute_thenPaymentIsSaved() {
        Payment payment = PaymentTestData.validPayment();
        useCase().execute(payment);
        Mockito.verify(paymentRepository).save(payment);
    }

    @Test
    void givenValidPayment_whenExecute_thenStatusIsPending() {
        Payment payment = PaymentTestData.validPayment();
        useCase().execute(payment);
        Assertions.assertEquals(PaymentStatus.PENDING, payment.getStatus());
        Assertions.assertNotNull(payment.getCreatedAt());
    }

    @Test
    void givenInvalidPayment_whenExecute_thenThrowsAndNothingSaved() {
        Payment payment = PaymentTestData.paymentWithEverythingWrong();
        Assertions.assertThrows(SystemViolationException.class, () -> useCase().execute(payment));
        Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any());
    }
}