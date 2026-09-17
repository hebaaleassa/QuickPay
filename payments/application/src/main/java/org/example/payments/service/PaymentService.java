package org.example.payments.service;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.repository.PaymentRepository;
import org.example.useCases.CreatePaymentUseCase;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final PaymentMapper mapper;
    private final PaymentRepository repository;

    public PaymentService(CreatePaymentUseCase createPaymentUseCase, PaymentMapper mapper, PaymentRepository repository) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.mapper = mapper;
        this.repository = repository;
    }

    public PaymentEntity save(PaymentEntity payment) {
        return mapper.toEntity(createPaymentUseCase.execute(mapper.toDomain(payment)));
    }

    public Optional<PaymentEntity> findBy(Long id) {
        return Optional.ofNullable(mapper.toEntity(repository.findBy(id)));
    }

    public List<PaymentEntity> findAll() {
        return (repository.findAll().stream().map(
                mapper::toEntity
        ).toList());
    }


}
