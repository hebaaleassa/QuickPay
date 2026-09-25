package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.PaymentTestData;
import com.progressoft.quickpay.payments.domain.exception.InvalidSortFieldException;
import com.progressoft.quickpay.payments.domain.exception.SystemViolationException;
import com.progressoft.quickpay.payments.domain.model.payment.BulkUploadResult;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.domain.usecases.CreatePaymentUseCase;
import com.progressoft.quickpay.payments.domain.usecases.UploadBulkPaymentUseCase;
import com.progressoft.quickpay.payments.repository.PaymentSearchRepository;
import com.progressoft.quickpay.payments.repository.models.PaymentFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.PaymentSortField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final Long payment_id = PaymentTestData.PAYMENT_ID;
    @Mock
    private CreatePaymentUseCase createPaymentUseCase;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentSearchRepository searchRepository;
    @Mock
    private UploadBulkPaymentUseCase uploadBulkPaymentUseCase;
    @InjectMocks
    private PaymentService service;

    @Test
    void givenValidPayment_whenCreate_thenUseCaseIsCalled() {
        Payment payment = PaymentTestData.validPayment();
        service.create(payment);
        Mockito.verify(createPaymentUseCase).execute(payment);
    }

    @Test
    void givenNegativeAmount_whenCreate_thenExceptionIsThrown() {
        Payment payment = PaymentTestData.paymentWithNegativeAmount();
        Mockito.doThrow(SystemViolationException.class)
                .when(createPaymentUseCase).execute(payment);
        Assertions.assertThrows(SystemViolationException.class, () -> service.create(payment));
    }

    @Test
    void givenPaymentExists_whenFindBy_thenReturnPayment() {
        Payment payment = PaymentTestData.savedPayment(payment_id);
        Mockito.when(paymentRepository.findBy(payment_id)).thenReturn(Optional.of(payment));
        Optional<Payment> result = service.findBy(payment_id);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(payment_id, result.get().getId());
    }

    @Test
    void givenPaymentMissing_whenFindBy_thenReturnEmpty() {
        Mockito.when(paymentRepository.findBy(payment_id)).thenReturn(Optional.empty());
        Assertions.assertTrue(service.findBy(payment_id).isEmpty());
    }

    @Test
    void givenPaymentsExist_whenFindAll_thenReturnAll() {
        List<Payment> payments = List.of(
                PaymentTestData.savedPayment(payment_id),
                PaymentTestData.savedPayment(payment_id + 1));
        Mockito.when(paymentRepository.findAll()).thenReturn(payments);
        Assertions.assertEquals(2, service.findAll().size());
    }

    @Test
    void givenValidSort_whenFindAll_thenCallSearchRepository() {
        PaymentFilter filter = new PaymentFilter(null, null, null, null,
                null, null, null, null, null, null, null);
        PagingOptions pagingOptions = new PagingOptions(0, 2);
        PagingResult<Payment> result = new PagingResult<>(List.of(), 0, 2, 0, 0);
        Mockito.when(searchRepository.findAll(filter, pagingOptions, PaymentSortField.ID, "asc")).thenReturn(result);
        Assertions.assertSame(result, service.findAll(filter, pagingOptions, "id", "asc"));
    }

    @Test
    void givenTemplateAndFile_whenUploadBulk_thenUseCaseResultIsReturned() {
        Path file = Path.of("payments.csv");
        BulkUploadResult result = new BulkUploadResult(0, 0, List.of(), List.of());
        Mockito.when(uploadBulkPaymentUseCase.execute("default", file)).thenReturn(result);
        Assertions.assertSame(result, service.uploadBulk("default", file));
    }

    @Test
    void givenUnknownSortField_whenFindAll_thenThrowInvalidSortFieldException() {
        PaymentFilter filter = new PaymentFilter(null, null, null, null,
                null, null, null, null, null, null, null);
        Assertions.assertThrows(InvalidSortFieldException.class,
                () -> service.findAll(filter, new PagingOptions(0, 2), "foo", "asc"));
        Mockito.verifyNoInteractions(searchRepository);
    }

    @Test
    void givenSortFieldInDifferentCase_whenFindAll_thenSortFieldIsResolved() {
        PaymentFilter filter = new PaymentFilter(null, null, null, null,
                null, null, null, null, null, null, null);
        PagingOptions pagingOptions = new PagingOptions(0, 2);
        service.findAll(filter, pagingOptions, "CreditorName", "desc");
        Mockito.verify(searchRepository).findAll(filter, pagingOptions, PaymentSortField.CREDITOR_NAME, "desc");
    }
}
