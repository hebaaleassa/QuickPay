package com.progressoft.quickpay.payments;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.repository.PaymentRepository;
import com.progressoft.quickpay.payments.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Long payment_id = 100L;
    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentService service;

    @Test
    void givenValidPayment_whenCreate_thenSaveAndReturn() {
        PaymentEntity input = new PaymentEntity();
        PaymentEntity save = new PaymentEntity();
        save.setId(payment_id);
        Mockito.when(repository.save(input)).thenReturn(save);
        PaymentEntity results = service.create(input);
        Assertions.assertEquals(payment_id, results.getId());
        Mockito.verify(repository).save(input);
    }

    @Test
    void givenPaymentExists_whenFindById_thenReturn() {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(payment_id);
        Mockito.when(repository.findById(payment_id)).thenReturn(Optional.of(payment));
        Optional<PaymentEntity> result = service.findBy(payment_id);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void givenNoPayment_whenFindById_thenReturnEmpty() {
        Mockito.when(repository.findById(payment_id)).thenReturn(Optional.empty());
        Optional<PaymentEntity> result = service.findBy(payment_id);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenPaymentsExist_whenFindAll_thenReturnThem() {
        List<PaymentEntity> payments = List.of(new PaymentEntity(), new PaymentEntity());
        Mockito.when(repository.findAll()).thenReturn(payments);
        List<PaymentEntity> result = service.findAll();
        Assertions.assertEquals(payments, result);
    }
}
