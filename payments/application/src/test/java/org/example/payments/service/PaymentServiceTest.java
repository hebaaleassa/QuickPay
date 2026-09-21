package org.example.payments.service;

import org.example.model.BulkResult;
import org.example.model.Payment;
import org.example.payments.repository.PaymentRepositoryImpl;
import org.example.useCases.CreatePaymentUseCase;
import org.example.useCases.UploadBulkCommand;
import org.example.useCases.UploadBulkUseCse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CreatePaymentUseCase createPaymentUseCase;

    @Mock
    private PaymentRepositoryImpl repository;

    @Mock
    UploadBulkUseCse uploadBulkUseCase;

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

    @Test
    public void givenTemplate_whenUpload_thenReturnsThePaymentAndSuccess() {
        Path path = Paths.get("/tmp/test.txt");
        String fileName = "payment-default";

        BulkResult result = new BulkResult(5, 5, 0, Map.of(), List.of());
        when(uploadBulkUseCase.execute(any(UploadBulkCommand.class))).thenReturn(result);

        BulkResult serviceResult = paymentService.uploadBulk(fileName, path);
        assertEquals(result, serviceResult);
    }
}
