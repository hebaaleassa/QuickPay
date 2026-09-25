package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.domain.exception.InvalidSortFieldException;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryAdapter;
import com.progressoft.quickpay.payments.repository.TemplateSearchRepository;
import com.progressoft.quickpay.payments.repository.models.TemplateFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.DuplicateTemplateException;
import com.progressoft.training.fileparser.exception.FileParserException;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.DeleteTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import com.progressoft.training.fileparser.usecase.UpdateTemplateUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TemplateService {

    private final CreateTemplateUseCase createTemplateUseCase;
    private final GetTemplateUseCase getTemplateUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;
    private final TemplateRepositoryAdapter templateRepositoryAdapter;
    private final UpdateTemplateUseCase updateTemplateUseCase;
    private final TemplateSearchRepository templateSearchRepository;
    private final String defaultTemplateName;

    public TemplateService(CreateTemplateUseCase createTemplateUseCase, GetTemplateUseCase getTemplateUseCase,
                           DeleteTemplateUseCase deleteTemplateUseCase, TemplateRepositoryAdapter templateRepositoryAdapter,
                           UpdateTemplateUseCase updateTemplateUseCase, TemplateSearchRepository templateSearchRepository,
                           @Value("${payments.default-template-name:default}") String defaultTemplateName) {

        this.createTemplateUseCase = createTemplateUseCase;
        this.getTemplateUseCase = getTemplateUseCase;
        this.deleteTemplateUseCase = deleteTemplateUseCase;
        this.templateRepositoryAdapter = templateRepositoryAdapter;
        this.updateTemplateUseCase = updateTemplateUseCase;
        this.templateSearchRepository = templateSearchRepository;
        this.defaultTemplateName = defaultTemplateName;
    }

    public Template create(Template template) {
        log.info("Creating template");
        if (isDefault(template.name())) {
            throw new DuplicateTemplateException(template.name());
        }
        return createTemplateUseCase.execute(template);
    }

    public Optional<Template> findByName(String name) {
        return getTemplateUseCase.execute(name);
    }

    public Optional<Template> findById(Long id) {
        return templateRepositoryAdapter.findById(id);
    }

    public List<Template> findAll() {
        return templateRepositoryAdapter.findAll();
    }

    public void delete(String name) {
        log.info("Deleting template");
        rejectDefault(name);
        deleteTemplateUseCase.execute(name);
    }

    public Template update(Template template) {
        log.info("Updating template");
        rejectDefault(template.name());
        return updateTemplateUseCase.execute(template);
    }

    public PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions,
                                          String sortBy, String direction) {
        log.info("finding templates");
        TemplateSortField sortField = TemplateSortField.from(sortBy).orElseThrow(() -> new InvalidSortFieldException(sortBy));
        return templateSearchRepository.findAll(filter, pagingOptions, sortField, direction);
    }

    private boolean isDefault(String name) {
        return name.equals(defaultTemplateName);
    }

    private void rejectDefault(String name) {
        if (isDefault(name)) {
            throw new FileParserException("The default template is read-only");
        }
    }
}