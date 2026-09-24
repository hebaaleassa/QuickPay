package com.progressoft.quickpay.payments.domain.usecases;

import com.progressoft.quickpay.payments.domain.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.validation.AmountValidator;
import com.progressoft.quickpay.payments.domain.validation.ValidatorChain;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {
    @Mock
    private PaymentRepository paymentRepository;

    private CreatePaymentUseCase useCase() {
        return new CreatePaymentUseCase(
                paymentRepository,
                new ValidatorChain<Payment>(List.of(new AmountValidator())));
    }

    @Test
    void givenValidPayment_whenExecute_thenPaymentIsSaved() {
        Payment payment = PaymentTestData.validPayment();
        useCase().execute(payment);
        Mockito.verify(paymentRepository).save(payment);
    }

    @Test
    void givenValidPayment_whenExecute_thenStatusIsPendingAndCreated() {
        Payment payment = PaymentTestData.validPayment();
        useCase().execute(payment);
        Assertions.assertEquals(PaymentStatus.PENDING, payment.getStatus());
        Assertions.assertNotNull(payment.getCreatedAt());
    }
}