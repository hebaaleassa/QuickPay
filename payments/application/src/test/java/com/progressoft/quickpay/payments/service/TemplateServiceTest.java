package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryAdapter;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.DeleteTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import com.progressoft.training.fileparser.usecase.UpdateTemplateUseCase;
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
    private TemplateRepositoryAdapter templateRepository;

    @Mock
    private DeleteTemplateUseCase deleteTemplateUseCase;

    @Mock
    private UpdateTemplateUseCase updateTemplateUseCase;

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

    @Test
    void givenTemplateExists_whenFindById_thenReturnTemplate() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(templateRepository.findById(TemplateTestData.template_id))
                .thenReturn(Optional.of(template));
        Assertions.assertTrue(service.findById(TemplateTestData.template_id).isPresent());
    }

    @Test
    void givenTemplateName_whenDelete_thenUseCaseIsCalled() {
        service.delete(TemplateTestData.templateName);
        Mockito.verify(deleteTemplateUseCase).execute(TemplateTestData.templateName);
    }

    @Test
    void givenTemplate_whenUpdate_thenUseCaseIsCalled() {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(updateTemplateUseCase.execute(template)).thenReturn(template);
        Assertions.assertSame(template, service.update(template));
    }
}