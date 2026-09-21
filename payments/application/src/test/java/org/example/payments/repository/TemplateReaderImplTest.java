package org.example.payments.repository;

import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import com.progressoft.training.fileparser.repository.TemplateReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateReaderImplTest {

    @Mock
    private TemplateRepositoryImpl repository;

    @Mock
    JsonTemplateRepository jsonTemplateRepository;

    @InjectMocks
    private TemplateReaderImpl templateReaderImpl;

    private Template defaultTemplate;
    private Template customTemplate;
    @BeforeEach
    void setUp() {
        defaultTemplate = new Template("payment-default", List.of(
                new FieldDefinition("amount", 20, true)));

        customTemplate = new Template("custom Template", List.of(
                new FieldDefinition("amount", 20, true)));
    }


    @Test
    public void givenExistingName_whenFindByName_thenReturnTemplate() {
        String name = "custom Template";
        when(repository.findByName(name)).thenReturn(Optional.of(customTemplate));

        Optional<Template> result = templateReaderImpl.findByName(name);
        assertTrue(result.isPresent());
        assertEquals(customTemplate, result.get());

    }

    @Test
    public void givenNameEmpty_whenFindByName_thenReturnTemplateDefault() {
        String name = "";
        when(jsonTemplateRepository.findByName("payment-default")).thenReturn(Optional.of(defaultTemplate));

        Optional<Template> result = templateReaderImpl.findByName(name);
        assertTrue(result.isPresent());
        assertEquals(defaultTemplate, result.get());
    }

    @Test
    public void givenNameNull_whenFindByName_thenReturnTemplateDefault() {
        String name = null;
        when(jsonTemplateRepository.findByName("payment-default")).thenReturn(Optional.of(defaultTemplate));

        Optional<Template> result = templateReaderImpl.findByName(name);
        assertTrue(result.isPresent());
        assertEquals(defaultTemplate, result.get());
    }

    @Test
    public void givenNameBlank_whenFindByName_thenReturnTemplateDefault() {
        String name = "     ";
        when(jsonTemplateRepository.findByName("payment-default")).thenReturn(Optional.of(defaultTemplate));

        Optional<Template> result = templateReaderImpl.findByName(name);
        assertTrue(result.isPresent());
        assertEquals(defaultTemplate, result.get());
    }

    @Test
    public void givenDefaultName_whenFindByName_thenReturnTemplateDefault() {
        String name = "payment-default";
        when(jsonTemplateRepository.findByName("payment-default")).thenReturn(Optional.of(defaultTemplate));

        Optional<Template> result = templateReaderImpl.findByName(name);
        assertTrue(result.isPresent());
        assertEquals(defaultTemplate, result.get());
    }

    @Test
    public void givenValid_whenFindAll_thenReturnTemplates() {
        List<Template> templates = List.of(defaultTemplate, customTemplate);
        when(repository.findAll()).thenReturn(templates);
        List<Template> result = templateReaderImpl.findAll();
        assertEquals(templates, result);
    }

}