package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.quickpay.payments.repository.jpa.TemplateRepositoryJpa;
import com.progressoft.training.fileparser.domain.Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

}