package org.example.payments.service;

import org.example.model.Payment;
import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.payments.repository.PaymentRepositoryImpl;
import org.example.useCases.CreatePaymentUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CreatePaymentUseCase createPaymentUseCase;

    @Mock
    private PaymentRepositoryImpl repository;

    @Mock
    private Payment payment;

    @InjectMocks
    private PaymentService paymentService;

    Long id;
    @BeforeEach
    void setUp() {
        id = 12L;
    }

    @Test
    void givenPayment_whenCreateSaves_thenReturnsThePaymentAndSuccess() {
        when(createPaymentUseCase.execute(payment)).thenReturn(payment);

        Payment result = paymentService.save(payment);

        assertNotNull(result);
        assertEquals(payment, result);
    }

    @Test
    void givenPaymentId_whenFindId_thenReturnsThePaymentAndSuccess() {
        when(repository.findBy(id)).thenReturn(payment);

        Optional<Payment> result = paymentService.findBy(id);

        assertTrue(result.isPresent());
        assertEquals(payment, result.get());
    }

    @Test
    void givenPayment_whenFindAll_thenReturnsThePaymentsAndSuccess() {
        when(repository.findAll()).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void givenInvalidPaymentId_whenFindId_thenReturnsResultEmpty() {
        when(repository.findBy(id)).thenReturn(null);

        Optional<Payment> result = paymentService.findBy(id);

        assertTrue(result.isEmpty());
    }
}
