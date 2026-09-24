package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.domain.exception.InvalidPagingException;
import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.quickpay.payments.repository.jpa.TemplateRepositoryJpa;
import com.progressoft.quickpay.payments.repository.models.TemplateFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.TemplateNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

class TemplateRepositoryAdapterTest {

    private TemplateRepositoryJpa repositoryJpa;
    private TemplateMapper mapper;
    private TemplateRepositoryAdapter repository;

    @BeforeEach
    void setUp() {
        repositoryJpa = Mockito.mock(TemplateRepositoryJpa.class);
        mapper = Mockito.mock(TemplateMapper.class);
        repository = new TemplateRepositoryAdapter(repositoryJpa, mapper);
    }

    @Test
    void givenTemplateExists_whenFindByName_thenReturnTemplate() {

        TemplateEntity entity = TemplateTestData.entity();
        Template template = TemplateTestData.validTemplate();
        Mockito.when(repositoryJpa.findByName(TemplateTestData.templateName)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDomain(entity)).thenReturn(template);
        Assertions.assertTrue(repository.findByName(TemplateTestData.templateName).isPresent());
    }

    @Test
    void givenTemplate_whenSave_thenCallJpaSave() {

        Template template = TemplateTestData.validTemplate();
        TemplateEntity entity = TemplateTestData.entity();
        Mockito.when(mapper.toEntity(template)).thenReturn(entity);
        Mockito.when(repositoryJpa.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDomain(entity)).thenReturn(template);
        repository.save(template);
        Mockito.verify(repositoryJpa).save(entity);
    }

    @Test
    void givenInvalidDirection_whenFindAll_thenThrowInvalidPagingException() {
        TemplateFilter filter = new TemplateFilter(null, null);
        Assertions.assertThrows(InvalidPagingException.class,
                () -> repository.findAll(filter, new PagingOptions(0, 2),
                        TemplateSortField.ID, "random"));
    }

    @Test
    void givenTemplateMissing_whenDelete_thenThrowException() {
        Mockito.when(repositoryJpa.findByName(TemplateTestData.templateName)).thenReturn(Optional.empty());
        Assertions.assertThrows(TemplateNotFoundException.class, () -> repository.deleteByName(TemplateTestData.templateName));
    }

    @Test
    void givenTemplateExists_whenFindById_thenReturnTemplate() {
        TemplateEntity entity = TemplateTestData.entity();
        Template template = TemplateTestData.validTemplate();
        Mockito.when(repositoryJpa.findById(TemplateTestData.template_id)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDomain(entity)).thenReturn(template);
        Assertions.assertTrue(repository.findById(TemplateTestData.template_id).isPresent());
    }

    @Test
    void givenTemplateFilter_whenFindAll_thenApplySpecification() {
        TemplateFilter filter = new TemplateFilter(TemplateTestData.template_id, TemplateTestData.templateName);
        Mockito.when(repositoryJpa.findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class))).thenReturn(Page.empty());
        PagingResult<Template> result = repository.findAll(filter, new PagingOptions(0, 2), TemplateSortField.ID, "asc");
        Assertions.assertTrue(result.content().isEmpty());
        Mockito.verify(repositoryJpa).findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class));
    }
}