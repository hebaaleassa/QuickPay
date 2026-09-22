package org.example.payments.repository;


import org.example.payments.model.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempateJpaRepository extends JpaRepository<TemplateEntity, Long> {
    Optional<TemplateEntity> findByName(String name);

}
