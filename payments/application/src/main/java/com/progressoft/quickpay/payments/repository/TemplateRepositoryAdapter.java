package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.filteration.TemplateFilter;
import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.quickpay.payments.paging.PagingOptions;
import com.progressoft.quickpay.payments.paging.PagingResult;
import com.progressoft.quickpay.payments.repository.jpa.TemplateRepositoryJpa;
import com.progressoft.quickpay.payments.sorting.TemplateSortField;
import com.progressoft.quickpay.payments.specification.SearchCriteria;
import com.progressoft.quickpay.payments.specification.TemplateSpecification;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.TemplateNotFoundException;
import com.progressoft.training.fileparser.repository.TemplateRepositoryInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TemplateRepositoryAdapter implements TemplateRepositoryInterface, TemplateSearchRepository {

    private final TemplateRepositoryJpa repository;
    private final TemplateMapper mapper;

    public TemplateRepositoryAdapter(TemplateRepositoryJpa repository, TemplateMapper mapper) {
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

    @Override
    public PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions, TemplateSortField sortField, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField.fieldName());
        PageRequest pageRequest = PageRequest.of(pagingOptions.pageNumber(), pagingOptions.pageSize(), sort);
        Page<TemplateEntity> page = repository.findAll(createFilter(filter), pageRequest);
        return toPagingResult(page);
    }

    private TemplateEntity updateFields(TemplateEntity exists, Template template) {
        exists.getFields().clear();
        exists.getFields().addAll(mapper.toEmbeddable(template.fields()));
        return exists;
    }

    private Specification<TemplateEntity> createFilter(TemplateFilter filter) {
        return Specification.allOf(createSpecification("id", "=", filter.id()),
                createSpecification("name", "like", filter.name()));
    }

    private Specification<TemplateEntity> createSpecification(String field, String operation, Object value) {
        if (value == null) {
            return Specification.unrestricted();
        }
        return new TemplateSpecification(new SearchCriteria(field, operation, value));
    }

    private PagingResult<Template> toPagingResult(Page<TemplateEntity> page) {
        return new PagingResult<>(page.map(mapper::toDomain).getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}