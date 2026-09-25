package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.PaymentTestData;
import com.progressoft.quickpay.payments.domain.exception.InvalidPagingException;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import com.progressoft.quickpay.payments.repository.jpa.PaymentRepositoryJpa;
import com.progressoft.quickpay.payments.repository.models.PaymentFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.PaymentSortField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

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

    @Test
    void givenPaymentDoesntExist_whenFindById_thenReturnEmpty() {
        Mockito.when(paymentRepositoryJpa.findById(payment_id)).thenReturn(Optional.empty());
        Optional<Payment> result = repository.findBy(payment_id);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void givenPaymentsExist_whenFindAll_thenReturnPayments() {
        PaymentEntity entity = PaymentTestData.savedEntity(payment_id);
        Payment payment = PaymentTestData.savedPayment(payment_id);
        Mockito.when(paymentRepositoryJpa.findAll()).thenReturn(List.of(entity));
        Mockito.when(paymentMapper.toDomain(entity)).thenReturn(payment);
        List<Payment> result = repository.findAll();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(payment_id, result.getFirst().getId());
    }

    @Test
    void givenInvalidDirection_whenFindAll_thenThrowInvalidPagingException() {
        PaymentFilter filter = new PaymentFilter(null, null, null, null, null,
                null, null, null, null, null, null);
        PagingOptions pagingOptions = new PagingOptions(0, 2);
        Assertions.assertThrows(InvalidPagingException.class, () -> repository.findAll(filter, pagingOptions,
                PaymentSortField.ID, "random"));
    }

    @Test
    void givenSenderFilter_whenFindAll_thenReturnPagingResult() {
        PaymentFilter filter = new PaymentFilter(null, "ACC-1", null, null, null,
                null, null, null, null, null, null);
        Page<PaymentEntity> page = Page.empty();
        Mockito.when(paymentRepositoryJpa.findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class))).thenReturn(page);
        PagingResult<Payment> result = repository.findAll(filter, new PagingOptions(0, 2), PaymentSortField.ID, "asc");
        Assertions.assertTrue(result.content().isEmpty());
    }

    @Test
    void givenPayments_whenSaveAll_thenReturnSavedPaymentsWithIds() {
        Payment payment = PaymentTestData.validPayment();
        PaymentEntity entity = PaymentTestData.newEntity();
        PaymentEntity savedEntity = PaymentTestData.savedEntity(payment_id);
        Payment saved = PaymentTestData.savedPayment(payment_id);
        Mockito.when(paymentMapper.toEntity(payment)).thenReturn(entity);
        Mockito.when(paymentRepositoryJpa.saveAll(List.of(entity))).thenReturn(List.of(savedEntity));
        Mockito.when(paymentMapper.toDomain(savedEntity)).thenReturn(saved);

        Assertions.assertEquals(List.of(saved), repository.saveAll(List.of(payment)));
    }
}
