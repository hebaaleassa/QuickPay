package org.example.payments.repository;

import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import org.example.payments.mapper.TemplateMapper;

import org.example.payments.model.TemplateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateRepositoryImplTest {

    @Mock
    private TempateJpaRepository jpaRepository;

    @Mock
    private TemplateMapper mapper;

    @InjectMocks
    private TemplateRepositoryImpl repository;


    Template template;
    @BeforeEach
    void setUp() {
        template = new Template("payment-default", List.of(
                new FieldDefinition("amount", 20, true)));

    }

    @Test
    public void givenNewTemplate_whenSave_thenCreate() {

        TemplateEntity entity = new TemplateEntity();

        when(jpaRepository.findByName("payment-default"))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(template)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(template);

        Template result = repository.save(template);

        assertEquals(template, result);
    }



    @Test
    public void givenName_whenFindByName_thenFind() {
        TemplateEntity  entity = new TemplateEntity();

        when(jpaRepository.findByName("payment-custom")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(template);

        Template result = repository.findByName("payment-custom").get();
        assertEquals(template, result);
        verify(jpaRepository).findByName("payment-custom");
    }


    @Test
    public void givenId_whenFindByName_thenFind() {
        TemplateEntity  entity = new TemplateEntity();


        when(jpaRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(template);

        Template result = repository.findBy(5L).get();
        assertEquals(template, result);
    }

    @Test
    public void givenName_whenDelete_thenDelete() {
        TemplateEntity  entity = new TemplateEntity();
        when(jpaRepository.findByName("payment-custom")).thenReturn(Optional.of(entity));
        repository.deleteByName("payment-custom");
        verify(jpaRepository).delete(entity);
    }

    @Test
    public void givenValidInput_whenFindBy_thenAllTemplate() {
        TemplateEntity  entity = new TemplateEntity();
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(template);

        List<Template> all = repository.findAll();
        assertEquals(List.of(template), all);
    }
}