package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.repository.TemplateRepositoryImpl;
import com.progressoft.training.fileparser.domain.ListTemplatesQuery;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TemplateService {

    private final CreateTemplateUseCase createTemplateUseCase;
    private final GetTemplateUseCase getTemplateUseCase;
    private final ListTemplatesUseCase listTemplatesUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;
    private final TemplateRepositoryImpl templateRepository;
    private final UpdateTemplateUseCase updateTemplateUseCase;

    public TemplateService(CreateTemplateUseCase createTemplateUseCase, GetTemplateUseCase getTemplateUseCase, ListTemplatesUseCase listTemplatesUseCase, DeleteTemplateUseCase deleteTemplateUseCase, TemplateRepositoryImpl templateRepository, UpdateTemplateUseCase updateTemplateUseCase) {

        this.createTemplateUseCase = createTemplateUseCase;
        this.getTemplateUseCase = getTemplateUseCase;
        this.listTemplatesUseCase = listTemplatesUseCase;
        this.deleteTemplateUseCase = deleteTemplateUseCase;
        this.templateRepository = templateRepository;
        this.updateTemplateUseCase = updateTemplateUseCase;
    }

    public Template create(Template template) {
        log.info("Creating template");
        return createTemplateUseCase.execute(template);
    }

    public Optional<Template> findByName(String name) {
        return getTemplateUseCase.execute(name);
    }

    public Optional<Template> findById(Long id) {
        return templateRepository.findById(id);
    }

    public List<Template> findAll() {
        return listTemplatesUseCase.execute(new ListTemplatesQuery());
    }

    public void delete(String name) {
        log.info("Deleting template");
        deleteTemplateUseCase.execute(name);
    }

    public Template update(Template template) {
        log.info("Updating template");
        return updateTemplateUseCase.execute(template);
    }
}