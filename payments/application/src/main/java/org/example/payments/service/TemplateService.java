package org.example.payments.service;

import com.progressoft.training.fileparser.domain.ListTemplatesQuery;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.*;
import org.example.model.PageResult;
import org.example.payments.repository.TemplateRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {
    private final TemplateRepositoryImpl repository;
    private final GetTemplateUseCase getTemplateUseCase;
    private final CreateTemplateUseCase createTemplateUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;
    private final UpdateTemplateUseCase updateTemplateUseCase;

    public TemplateService(TemplateRepositoryImpl repository,
                           GetTemplateUseCase getTemplateUseCase,
                           CreateTemplateUseCase createTemplateUseCase,
                           DeleteTemplateUseCase deleteTemplateUseCase,
                           UpdateTemplateUseCase updateTemplateUseCase) {
        this.repository = repository;
        this.getTemplateUseCase = getTemplateUseCase;
        this.createTemplateUseCase = createTemplateUseCase;
        this.deleteTemplateUseCase = deleteTemplateUseCase;
        this.updateTemplateUseCase = updateTemplateUseCase;
    }


    public Template create(Template template) {
        return createTemplateUseCase.execute(template);
    }

    public Optional<Template> findBy(Long id) {
        return repository.findBy(id);
    }

    public Optional<Template> findByName(String name) {
        return getTemplateUseCase.execute(name);
    }

    public List<Template> findAll() {
        return repository.findAll();
    }

    public PageResult<Template> findAll(int page, int size) {
        return repository.findAll(page, size);
    }

    public void delete(String name) {
        deleteTemplateUseCase.execute(name);
    }

    public Optional<Template> get(Template template) {
        return getTemplateUseCase.execute(template.name());
    }

    public Template update(Template template) {
        return updateTemplateUseCase.execute(template);
    }

}
