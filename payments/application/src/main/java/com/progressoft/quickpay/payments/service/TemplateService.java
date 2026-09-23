package com.progressoft.quickpay.payments.service;

import com.progressoft.quickpay.payments.domain.exception.InvalidSortFieldException;
import com.progressoft.quickpay.payments.domain.filteration.TemplateFilter;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;
import com.progressoft.quickpay.payments.domain.sorting.TemplateSortField;
import com.progressoft.quickpay.payments.repository.TemplateRepositoryAdapter;
import com.progressoft.quickpay.payments.repository.TemplateSearchRepository;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.usecase.CreateTemplateUseCase;
import com.progressoft.training.fileparser.usecase.DeleteTemplateUseCase;
import com.progressoft.training.fileparser.usecase.GetTemplateUseCase;
import com.progressoft.training.fileparser.usecase.UpdateTemplateUseCase;
import lombok.extern.slf4j.Slf4j;
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

    public TemplateService(CreateTemplateUseCase createTemplateUseCase, GetTemplateUseCase getTemplateUseCase,
                           DeleteTemplateUseCase deleteTemplateUseCase, TemplateRepositoryAdapter templateRepositoryAdapter,
                           UpdateTemplateUseCase updateTemplateUseCase, TemplateSearchRepository templateSearchRepository) {

        this.createTemplateUseCase = createTemplateUseCase;
        this.getTemplateUseCase = getTemplateUseCase;
        this.deleteTemplateUseCase = deleteTemplateUseCase;
        this.templateRepositoryAdapter = templateRepositoryAdapter;
        this.updateTemplateUseCase = updateTemplateUseCase;
        this.templateSearchRepository = templateSearchRepository;
    }

    public Template create(Template template) {
        log.info("Creating template");
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
        deleteTemplateUseCase.execute(name);
    }

    public Template update(Template template) {
        log.info("Updating template");
        return updateTemplateUseCase.execute(template);
    }

    public PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions,
                                          String sortBy, String direction) {
        log.info("finding templates");
        TemplateSortField sortField;
        try {
            sortField = TemplateSortField.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidSortFieldException(sortBy);
        }
        return templateSearchRepository.findAll(filter, pagingOptions, sortField, direction);
    }
}