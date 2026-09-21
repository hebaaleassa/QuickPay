package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.repository.PaymentRepository;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
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
    public void saveAll(List<Payment> payments) {
        List<PaymentEntity> entities = payments.stream().map(paymentMapper::toEntity).toList();
        paymentRepositoryJpa.saveAll(entities);
    }
}