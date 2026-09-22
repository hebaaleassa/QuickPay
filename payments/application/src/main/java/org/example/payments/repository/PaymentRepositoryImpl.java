package org.example.payments.repository;

import org.example.model.BulkResult;
import org.example.model.Payment;
import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.payments.service.PaymentService;
import org.example.repository.PaymentRepository;
import org.example.useCases.UploadBulkUseCse;
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
}
