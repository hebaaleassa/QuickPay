package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.training.fileparser.domain.ListTemplatesQuery;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import com.progressoft.training.fileparser.usecase.ListTemplatesUseCase;
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
    private ListTemplatesUseCase listTemplatesUseCase;

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
        Mockito.when(listTemplatesUseCase.execute(Mockito.any(ListTemplatesQuery.class))).thenReturn(List.of(TemplateTestData.validTemplate()));
        Assertions.assertEquals(1, service.findAll().size());
    }
}