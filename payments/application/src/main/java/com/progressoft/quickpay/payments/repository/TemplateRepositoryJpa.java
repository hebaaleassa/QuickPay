package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepositoryJpa extends JpaRepository<TemplateEntity, Long> {
    Optional<TemplateEntity> findByName(String name);
}
