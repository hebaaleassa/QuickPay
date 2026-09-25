package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.exception.InvalidPagingException;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import com.progressoft.quickpay.payments.repository.jpa.PaymentRepositoryJpa;
import com.progressoft.quickpay.payments.repository.models.PaymentFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.PaymentSortField;
import com.progressoft.quickpay.payments.specification.PaymentSpecification;
import com.progressoft.quickpay.payments.specification.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentRepositoryImpl implements PaymentRepository, PaymentSearchRepository {
    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final PaymentMapper paymentMapper;

    public PaymentRepositoryImpl(PaymentRepositoryJpa paymentRepositoryJpa,
                                 PaymentMapper paymentMapper) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public void save(Payment payment) {
        PaymentEntity entity = paymentMapper.toEntity(payment);
        PaymentEntity saved = paymentRepositoryJpa.save(entity);
        payment.setId(saved.getId());
    }

    @Override
    public Optional<Payment> findBy(Long id) {
        return paymentRepositoryJpa.findById(id).map(paymentMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepositoryJpa.findAll().stream().map(paymentMapper::toDomain).toList();
    }

    @Override
    public List<Payment> saveAll(List<Payment> payments) {
        List<PaymentEntity> entities = payments.stream().map(paymentMapper::toEntity).toList();
        return paymentRepositoryJpa.saveAll(entities).stream().map(paymentMapper::toDomain).toList();
    }

    @Override
    public PagingResult<Payment> findAll(PaymentFilter filter, PagingOptions pagingOptions, PaymentSortField sortField, String direction) {
        Sort.Direction sortingDirection = Sort.Direction.fromOptionalString(direction)
                .orElseThrow(() -> new InvalidPagingException("direction must be 'asc' or 'desc'"));
        Sort sort = Sort.by(sortingDirection, sortField.fieldName()).and(Sort.by("id"));
        PageRequest pageRequest = PageRequest.of(pagingOptions.pageNumber(), pagingOptions.pageSize(), sort);
        Page<PaymentEntity> page = paymentRepositoryJpa.findAll(createFilter(filter), pageRequest);

        return toPagingResult(page);
    }

    private Specification<PaymentEntity> createFilter(PaymentFilter filter) {
        return Specification.allOf(
                createSpecification("id", "=", filter.id()),
                createSpecification("senderAccount", "like", filter.senderAccount()),
                createSpecification("receiverAccount", "like", filter.receiverAccount()),
                createSpecification("currency", "=", filter.currency()),
                createSpecification("status", "=", filter.status()),
                createSpecification("notes", "like", filter.notes()),
                createSpecification("creditorName", "like", filter.creditorName()),
                createSpecification("createdAt", ">=", filter.createdFrom()),
                createSpecification("createdAt", "<=", filter.createdTo()),
                createSpecification("amount", ">=", filter.minAmount()),
                createSpecification("amount", "<=", filter.maxAmount()));
    }

    private Specification<PaymentEntity> createSpecification(String field, String operation, Object value) {
        if (value == null || value instanceof String text && text.isBlank()) {
            return Specification.unrestricted();
        }
        return new PaymentSpecification(new SearchCriteria(field, operation, value));
    }

    private PagingResult<Payment> toPagingResult(Page<PaymentEntity> page) {
        return new PagingResult<>(page.map(paymentMapper::toDomain).getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}