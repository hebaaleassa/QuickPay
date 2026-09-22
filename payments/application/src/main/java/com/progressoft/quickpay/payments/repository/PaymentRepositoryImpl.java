package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
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
public class PaymentRepositoryImpl implements PaymentRepository {
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
    public List<Payment> findAll(String sortBy, String direction) {
        Sort sorted = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return paymentRepositoryJpa.findAll(sorted).stream().map(paymentMapper::toDomain).toList();
    }

    @Override
    public PagingResult<Payment> findAll(PagingOptions pagingOptions) {
        PageRequest pageRequest = PageRequest.of(pagingOptions.pageNumber(), pagingOptions.pageSize());
        Page<PaymentEntity> page = paymentRepositoryJpa.findAll(pageRequest);
        return new PagingResult<>(page.map(paymentMapper::toDomain).getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<Payment> findAll(PaymentFilter filter) {
        Specification<PaymentEntity> specification = Specification.allOf(
                createSpecification("currency", "=", filter.currency()),
                createSpecification("status", "=", filter.status()),
                createSpecification("amount", ">=", filter.minAmount()),
                createSpecification("amount", "<=", filter.maxAmount()));
        return paymentRepositoryJpa.findAll(specification).stream().map(paymentMapper::toDomain).toList();
    }

    private Specification<PaymentEntity> createSpecification(String field, String operation, Object value) {
        if (value == null) {
            return Specification.unrestricted();
        }
        return new PaymentSpecification(new SearchCriteria(field, operation, value));
    }
}