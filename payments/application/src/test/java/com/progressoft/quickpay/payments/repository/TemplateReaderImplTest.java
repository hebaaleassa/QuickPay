package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class TemplateReaderImplTest {
    private JsonTemplateRepository jsonTemplateRepository;
    private TemplateRepositoryAdapter templateRepositoryAdapter;
    private TemplateReaderImpl templateReader;

    @BeforeEach
    void setUp() {
        jsonTemplateRepository = Mockito.mock(JsonTemplateRepository.class);
        templateRepositoryAdapter = Mockito.mock(TemplateRepositoryAdapter.class);
        templateReader = new TemplateReaderImpl(jsonTemplateRepository, templateRepositoryAdapter);
    }

    @Test
    void givenTemplateExists_whenFindByName_thenReturnTemplate() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(templateRepositoryAdapter.findByName(TemplateTestData.templateName)).thenReturn(Optional.of(template));
        Assertions.assertTrue(templateReader.findByName(TemplateTestData.templateName).isPresent());
    }

    @Test
    void givenTemplatesExist_whenFindAll_thenReturnAll() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(jsonTemplateRepository.findAll()).thenReturn(List.of(template));
        Mockito.when(templateRepositoryAdapter.findAll()).thenReturn(List.of(template));
        Assertions.assertEquals(2, templateReader.findAll().size());
    }
}

