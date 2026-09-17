package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.PaymentTestData;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImplTest {
    private static final Long payment_id = PaymentTestData.PAYMENT_ID;
    @Mock
    private PaymentRepositoryJpa paymentRepositoryJpa;
    @Mock
    private PaymentMapper paymentMapper;
    private PaymentRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PaymentRepositoryImpl(
                paymentRepositoryJpa, paymentMapper
        );
    }

    @Test
    void givenPaymentExists_whenFindBy_thenReturnPayment() {
        PaymentEntity entity = PaymentTestData.savedEntity(payment_id);
        Payment payment = PaymentTestData.savedPayment(payment_id);
        Mockito.when(paymentRepositoryJpa.findById(payment_id)).thenReturn(Optional.of(entity));
        Mockito.when(paymentMapper.toDomain(entity)).thenReturn(payment);
        Optional<Payment> result = repository.findBy(payment_id);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(payment_id, result.get().getId());
    }

    @Test
    void givenNoPayments_whenFindAll_thenReturnEmptyList() {
        Mockito.when(paymentRepositoryJpa.findAll()).thenReturn(List.of());
        Assertions.assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void givenValidPayment_whenSave_thenGeneratedIdIsSetforPayment() {
        Payment payment = PaymentTestData.validPayment();
        Mockito.when(paymentRepositoryJpa.save(Mockito.any()))
                .thenReturn(PaymentTestData.savedEntity(payment_id));
        repository.save(payment);
        Assertions.assertEquals(payment_id, payment.getId());
    }

    @Test
    void givenValidPayment_whenSave_thenJpaSaveIsCalled() {
        Mockito.when(paymentRepositoryJpa.save(Mockito.any()))
                .thenReturn(PaymentTestData.savedEntity(payment_id));
        repository.save(PaymentTestData.validPayment());
        Mockito.verify(paymentRepositoryJpa).save(Mockito.any());
    }
}