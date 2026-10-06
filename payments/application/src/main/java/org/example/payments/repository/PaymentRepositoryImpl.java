package org.example.payments.repository;

import org.example.model.BulkResult;
import org.example.model.PageResult;
import org.example.model.Payment;
import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.payments.service.PaymentService;
import org.example.repository.PaymentRepository;
import org.example.useCases.UploadBulkUseCse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper mapper;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository, PaymentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Payment> saveAll(List<Payment> payments) {
        List<PaymentEntity> entities = payments.stream().map(mapper::toEntity)
                .toList();

        List<PaymentEntity> savedAll = jpaRepository.saveAll(entities);
        return savedAll.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(payment)));
    }

    @Override
    public Payment findBy(Long id) {
        return mapper.toDomain(jpaRepository.findById(id).isPresent() ? jpaRepository.findById(id).get() : null);
    }

    @Override
    public List<Payment> findAll() {
        return (jpaRepository.findAll().stream().map(
                s -> mapper.toDomain(s)
        ).toList());
    }

    @Override
    public PageResult<Payment> findAll(int page, int size) {
        Page<PaymentEntity> result = jpaRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        List<Payment> content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(content, page, size, result.getTotalElements());
    }
}
