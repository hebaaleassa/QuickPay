package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.domain.exception.InvalidSortFieldException;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryAdapter;
import com.progressoft.quickpay.payments.repository.TemplateSearchRepository;
import com.progressoft.quickpay.payments.repository.models.TemplateFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.DuplicateTemplateException;
import com.progressoft.training.fileparser.exception.FileParserException;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.DeleteTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import com.progressoft.training.fileparser.usecase.UpdateTemplateUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

// Techniques: decision table on (operation x template name is the default one) -> allowed / rejected
@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    private static final String DEFAULT_TEMPLATE = "default";

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

    @Mock
    private TemplateSearchRepository templateSearchRepository;

    private TemplateService service;

    @BeforeEach
    void setUp() {
        service = new TemplateService(createTemplateUseCase, getTemplateUseCase, deleteTemplateUseCase,
                templateRepository, updateTemplateUseCase, templateSearchRepository, DEFAULT_TEMPLATE);
    }

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

    @Test
    void givenDefaultTemplateName_whenCreate_thenThrowDuplicateTemplateException() {
        Template template = new Template(DEFAULT_TEMPLATE, List.of(new FieldDefinition("a", 1, true)));
        Assertions.assertThrows(DuplicateTemplateException.class, () -> service.create(template));
        Mockito.verifyNoInteractions(createTemplateUseCase);
    }

    @Test
    void givenDefaultTemplateName_whenUpdate_thenRejectedAsReadOnly() {
        Template template = new Template(DEFAULT_TEMPLATE, List.of(new FieldDefinition("a", 1, true)));
        FileParserException exception = Assertions.assertThrows(FileParserException.class, () -> service.update(template));
        Assertions.assertEquals("The default template is read-only", exception.getMessage());
        Mockito.verifyNoInteractions(updateTemplateUseCase);
    }

    @Test
    void givenDefaultTemplateName_whenDelete_thenRejectedAsReadOnly() {
        FileParserException exception = Assertions.assertThrows(FileParserException.class, () -> service.delete(DEFAULT_TEMPLATE));
        Assertions.assertEquals("The default template is read-only", exception.getMessage());
        Mockito.verifyNoInteractions(deleteTemplateUseCase);
    }

    @Test
    void givenSortFieldInDifferentCase_whenFindAllPaged_thenSearchRepositoryIsCalled() {
        TemplateFilter filter = new TemplateFilter(null, "abc");
        PagingOptions pagingOptions = new PagingOptions(0, 2);
        PagingResult<Template> result = new PagingResult<>(List.of(), 0, 2, 0, 0);
        Mockito.when(templateSearchRepository.findAll(filter, pagingOptions, TemplateSortField.NAME, "desc")).thenReturn(result);
        Assertions.assertSame(result, service.findAll(filter, pagingOptions, "NAME", "desc"));
    }

    @Test
    void givenUnknownSortField_whenFindAllPaged_thenThrowInvalidSortFieldException() {
        Assertions.assertThrows(InvalidSortFieldException.class,
                () -> service.findAll(new TemplateFilter(null, null), new PagingOptions(0, 2), "fields", "asc"));
        Mockito.verifyNoInteractions(templateSearchRepository);
    }
}
