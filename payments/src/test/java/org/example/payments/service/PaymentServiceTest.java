package org.example.payments.service;

import org.example.payments.model.PaymentEntity;
import org.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentEntity payment;

    @BeforeEach
    void setUp() {
        payment = new PaymentEntity();
        payment.setAmount(new BigDecimal("100.00"));

    }

    @Test
    void givenPayment_whenCreateSaves_thenReturnsThePaymentAndSuccess() {
        PaymentEntity saved = new PaymentEntity();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("100.00"));

        Mockito.when(paymentRepository.save(payment)).thenReturn(saved);

        PaymentEntity result = paymentService.save(payment);

        assertEquals(1L, result.getId());
        Mockito.verify(paymentRepository).save(payment);
    }

    @Test
    void givenId_whenFindPaymentById_thenReturnsThePayment() {
        payment.setId(1L);
        Mockito.when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentEntity result = paymentService.findBy(1L).get();
        assertEquals(1L, result.getId());

        Mockito.verify(paymentRepository).findById(1L);
    }


    @Test
    void givenUndefinedId_whenFindPaymentById_thenNull() {

        Mockito.when(paymentRepository.findById(4L)).thenReturn(Optional.empty());

        Optional<PaymentEntity> result = paymentService.findBy(4L);
        assertEquals(Optional.empty(), result);

        Mockito.verify(paymentRepository).findById(4L);
    }

    @Test
    void givenAll_whenFindAllPayment_thenReturnListOfPayments(){
        Mockito.when(paymentRepository.findAll()).thenReturn(List.of(payment));
        List<PaymentEntity> paymentList = paymentService.findAll();
        assertEquals(1, paymentList.size());
        Mockito.verify(paymentRepository).findAll();
    }




}

