package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.TemplateNotFoundException;
import com.progressoft.training.fileparser.repository.TemplateRepositoryInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TemplateRepositoryImpl implements TemplateRepositoryInterface {

    private final TemplateRepositoryJpa repository;
    private final TemplateMapper mapper;

    public TemplateRepositoryImpl(TemplateRepositoryJpa repository, TemplateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Template> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    public Optional<Template> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Template> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Template save(Template template) {
        TemplateEntity entity = repository.findByName(template.name()).map(
                exists -> updateFields(exists, template)).orElseGet(() -> mapper.toEntity(template));
        TemplateEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByName(String name) {
        TemplateEntity entity = repository.findByName(name).orElseThrow(() -> new TemplateNotFoundException(name));
        repository.delete(entity);
    }

    private TemplateEntity updateFields(TemplateEntity exists, Template template) {
        exists.getFields().clear();
        exists.getFields().addAll(mapper.toEmbeddable(template.fields()));
        return exists;
    }
}