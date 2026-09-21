package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        Optional<PaymentEntity> entity = paymentRepositoryJpa.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(paymentMapper.toDomain(entity.get()));
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        for (PaymentEntity entity : paymentRepositoryJpa.findAll()) {
            payments.add(paymentMapper.toDomain(entity));
        }
        return payments;
    }

    @Override
    public void saveAll(List<Payment> payments) {
        List<PaymentEntity> entities = new ArrayList<>();

        for (Payment payment : payments) {
            entities.add(paymentMapper.toEntity(payment));
        }
        paymentRepositoryJpa.saveAll(entities);
    }
}