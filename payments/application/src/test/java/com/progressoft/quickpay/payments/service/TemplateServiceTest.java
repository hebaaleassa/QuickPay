package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryImpl;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private CreateTemplateUseCase createTemplateUseCase;

    @Mock
    private GetTemplateUseCase getTemplateUseCase;

    @Mock
    private TemplateRepositoryImpl templateRepository;

    @InjectMocks
    private TemplateService service;

    @Test
    void givenTemplate_whenCreate_thenUseCaseIsCalled() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(createTemplateUseCase.execute(template)).thenReturn(template);
        service.create(template);
        Mockito.verify(createTemplateUseCase).execute(template);
    }

    @Test
    void givenTemplateExists_whenFindByName_thenReturnTemplate() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(getTemplateUseCase.execute(TemplateTestData.templateName)).thenReturn(Optional.of(template));
        Assertions.assertTrue(service.findByName(TemplateTestData.templateName).isPresent());
    }

    @Test
    void givenTemplatesExist_whenFindAll_thenReturnAll() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(templateRepository.findAll()).thenReturn(List.of(template));
        Assertions.assertEquals(1, service.findAll().size());
    }
}