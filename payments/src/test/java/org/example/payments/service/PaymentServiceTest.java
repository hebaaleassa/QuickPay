package org.example.payments.service;

import org.example.payments.model.Payment;
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

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));

    }

    @Test
    void givenPayment_whenCreateSaves_thenReturnsThePaymentAndSuccess() {
        Payment saved = new Payment();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("100.00"));

        Mockito.when(paymentRepository.save(payment)).thenReturn(saved);

        Payment result = paymentService.SavePayment(payment);

        assertEquals(1L, result.getId());
        Mockito.verify(paymentRepository).save(payment);
    }

    @Test
    void givenId_whenFindPaymentById_thenReturnsThePayment() {
        payment.setId(1L);
        Mockito.when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment result = paymentService.findPaymentById(1L).get();
        assertEquals(1L, result.getId());

        Mockito.verify(paymentRepository).findById(1L);
    }


    @Test
    void givenUndefinedId_whenFindPaymentById_thenNull() {
        Mockito.when(paymentRepository.findById(4L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentService.findPaymentById(4L);
        assertEquals(Optional.empty(), result);

        Mockito.verify(paymentRepository).findById(4L);
    }

    @Test
    void givenAll_whenFindAllPayment_thenReturnListOfPayments(){
        Mockito.when(paymentRepository.findAll()).thenReturn(List.of(payment));
        List<Payment> paymentList = paymentService.findAllPayment();
        assertEquals(1, paymentList.size());
        Mockito.verify(paymentRepository).findAll();
    }




}

