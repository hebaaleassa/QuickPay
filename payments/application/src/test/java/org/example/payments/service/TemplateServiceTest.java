package org.example.payments.service;

import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.*;
import org.example.payments.repository.TemplateRepositoryImpl;
import org.example.payments.resource.TemplateResponse;
import org.hibernate.mapping.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    CreateTemplateUseCase createTemplateUseCase;

    @Mock
    DeleteTemplateUseCase deleteTemplateUseCase;

    @Mock
    UpdateTemplateUseCase  updateTemplateUseCase;

    @Mock
    GetTemplateUseCase getTemplateUseCase;

    @Mock
    TemplateRepositoryImpl repository;



    @InjectMocks
    TemplateService templateService;

    Template template;
    @BeforeEach
    void setUp() {
        template = new Template("payment-default", List.of(
                new FieldDefinition("senderAccount", 34, true),
                new FieldDefinition("receiverAccount", 34, true),
                new FieldDefinition("amount", 20, true),
                new FieldDefinition("currency", 3, true),
                new FieldDefinition("notes", 500, false),
                new FieldDefinition("creditorName", 100, true)
                ));

    }

    @Test
    void givenTemplate_whenCreateTemplate_thenSuccessCreateAndReturnTemplate() {
        when(createTemplateUseCase.execute(this.template)).thenReturn(template);
        Template result = templateService.create(this.template);
        assertNotNull(result);
        assertEquals(result, template);
    }

    @Test
    void givenId_whenFindBy_thenSuccess() {

    }

    @Test
    void givenName_whenFindByName_thenSuccessReturnTemplate() {
        when(getTemplateUseCase.execute("payment-default")).thenReturn(Optional.of(template));
        Optional<Template> result = templateService.findByName("payment-default");
        assertTrue(result.isPresent());
        assertEquals(result.get().name(), template.name());

    }

    @Test
    void givenName_whenFindByName_thenFailureReturnNotFound() {
        when(getTemplateUseCase.execute("notFoundFile")).thenReturn(Optional.empty());
        Optional<Template> result = templateService.findByName("notFoundFile");
        assertFalse(result.isPresent());
    }

    @Test
    void giveValid_whenFindAll_thenSuccessReturnTemplates() {
        when(repository.findAll()).thenReturn(List.of(template));
        List<Template> result = templateService.findAll();
        assertNotNull(result);
        assertEquals(result, List.of(template));
    }

    @Test
    void givenName_whenDelete_thenSuccessDelete() {
        templateService.delete("payment-default");
        verify(deleteTemplateUseCase).execute("payment-default");

    }


    @Test
    void givenTemplate_whenGet_thenSuccessReturnTemplate() {
        when(getTemplateUseCase.execute(template.name())).thenReturn(Optional.of(template));
        Optional<Template> result = templateService.get(template);
        assertTrue(result.isPresent());
    }

    @Test
    void givenTemplate_whenUpdate_thenSuccessUpdate() {
        when(updateTemplateUseCase.execute(template)).thenReturn(template);
        Template update = templateService.update(template);
        assertNotNull(update);
        assertEquals(update, template);
    }
}