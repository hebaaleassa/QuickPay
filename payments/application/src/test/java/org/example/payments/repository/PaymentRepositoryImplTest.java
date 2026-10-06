package org.example.payments.repository;

import org.example.model.PageResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.example.model.Payment;
import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.payments.service.PaymentService;
import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentRepositoryImplTest {

    @Mock
    private PaymentJpaRepository repository;

    @Mock
    private PaymentMapper mapper;

    @Mock
    private Payment paymentDomain;

    @Mock
    private PaymentEntity paymentEntity;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenPaymentDomain_whenSave_thenSaveEntity() {

        when(mapper.toEntity(paymentDomain)).thenReturn(Optional.of(paymentEntity).get());
        when(repository.save(paymentEntity)).thenReturn(paymentEntity);
        when(mapper.toDomain(paymentEntity)).thenReturn(Optional.of(paymentDomain).get());

        Payment result = paymentRepositoryImpl.save(paymentDomain);

        assertNotNull(result);
        assertEquals(paymentDomain, result);
        verify(mapper).toEntity(paymentDomain);
        verify(mapper).toDomain(paymentEntity);
    }

    @Test
    void givenPaymentDomain_whenFindById_thenFindEntity() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.of(paymentEntity));
        when(mapper.toDomain(paymentEntity)).thenReturn(paymentDomain);

        Payment result = paymentRepositoryImpl.findBy(id);

        assertNotNull(result);
        assertEquals(paymentDomain, result);
        verify(mapper).toDomain(paymentEntity);
        verify(mapper).toDomain(paymentEntity);

    }

    @Test
    void givenNonExistingId_whenFindById_thenReturnNull() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        Payment result = paymentRepositoryImpl.findBy(id);

        assertNull(result);
        verify(repository).findById(id);
    }

    @Test
    public  void givenPaymentDomain_whenFindAll_thenFindAllEntities() {
        when(repository.findAll()).thenReturn(List.of(paymentEntity));
        when(mapper.toDomain(paymentEntity)).thenReturn(paymentDomain);

        List<Payment> result = paymentRepositoryImpl.findAll();

        assertNotNull(result);
        assertEquals(paymentDomain, result.getFirst());
        verify(mapper).toDomain(paymentEntity);

    }

    @Test
    public void givenPageAndSize_whenFindAllPaged_thenReturnsMappedPage() {
        when(repository.findAll(PageRequest.of(1, 5, Sort.by("id"))))
                .thenReturn(new PageImpl<>(List.of(paymentEntity), PageRequest.of(1, 5), 6));
        when(mapper.toDomain(paymentEntity)).thenReturn(paymentDomain);

        PageResult<Payment> result = paymentRepositoryImpl.findAll(1, 5);

        assertEquals(List.of(paymentDomain), result.content());
        assertEquals(6, result.totalElements());
        assertEquals(1, result.page());
    }

    @Test
    public  void givenListOfPayments_whenSaveAll_thenSaveAndReturnAll() {
        when(mapper.toEntity(paymentDomain)).thenReturn(Optional.of(paymentEntity).get());
        when(repository.saveAll(List.of(paymentEntity))).thenReturn(List.of(paymentEntity));
        when(mapper.toDomain(paymentEntity)).thenReturn(Optional.of(paymentDomain).get());

        List<Payment> result = paymentRepositoryImpl.saveAll(List.of(paymentDomain));

        assertNotNull(result);
        assertEquals(List.of(paymentDomain), result);
    }
}