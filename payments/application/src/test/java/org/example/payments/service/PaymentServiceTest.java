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
    private PaymentRepositoryImpl paymentRepository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private  CreatePaymentUseCase createPaymentUseCase;

    @Mock
    private PaymentEntity paymentEntity;

    @Mock
    private Payment paymentDomain;

    @InjectMocks
    private PaymentService paymentService;


    Long id;
    @BeforeEach
    void setUp() {
        id = 12L;
    }

    @Test
    void givenPaymentEntity_whenCreateSaves_thenReturnsThePaymentAndSuccess() {

        when(mapper.toDomain(paymentEntity)).thenReturn(paymentDomain);
        when(createPaymentUseCase.execute(paymentDomain)).thenReturn(paymentDomain);
        when(mapper.toEntity(paymentDomain)).thenReturn(paymentEntity);

        PaymentEntity result = paymentService.save(paymentEntity);
        assertEquals(paymentEntity, result);
       verify(createPaymentUseCase).execute(paymentDomain);
    }


    @Test
    void givenPaymentId_whenFindId_thenReturnsThePaymentAndSuccess() {
        when(paymentRepository.findBy(id)).thenReturn(paymentDomain);
        when(mapper.toEntity(paymentDomain)).thenReturn(paymentEntity);

        PaymentEntity result = paymentService.findBy(id).get();
        assertEquals(paymentEntity, result);
    }

    @Test
    void givenInvalidPaymentId_whenFindId_thenReturnsResultEmpty() {
        when(paymentRepository.findBy(id)).thenReturn(null);

        Optional<PaymentEntity> result = paymentService.findBy(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void givenPayment_whenFindAll_thenReturnsThePaymentsAndSuccess() {
        when(paymentRepository.findAll()).thenReturn(List.of(paymentDomain));
        List<PaymentEntity> result = paymentService.findAll();
        assertFalse(result.isEmpty());
        verify(paymentRepository).findAll();
    }




//    @Test
//    void givenId_whenFindPaymentById_thenReturnsThePayment() {
//        payment.setId(1L);
//        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
//
//        PaymentEntity result = paymentService.findBy(1L).get();
//        assertEquals(1L, result.getId());
//
//        Mockito.verify(paymentRepository).findById(1L);
//    }
//
//
//    @Test
//    void givenUndefinedId_whenFindPaymentById_thenNull() {
//
//        when(paymentRepository.findById(4L)).thenReturn(Optional.empty());
//
//        Optional<PaymentEntity> result = paymentService.findBy(4L);
//        assertEquals(Optional.empty(), result);
//
//        Mockito.verify(paymentRepository).findById(4L);
//    }
//
//    @Test
//    void givenAll_whenFindAllPayment_thenReturnListOfPayments(){
//        when(paymentRepository.findAll()).thenReturn(List.of(payment));
//        List<PaymentEntity> paymentList = paymentService.findAll();
//        assertEquals(1, paymentList.size());
//        Mockito.verify(paymentRepository).findAll();
//    }




}

