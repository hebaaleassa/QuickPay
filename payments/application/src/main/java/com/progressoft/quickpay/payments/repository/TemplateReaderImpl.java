package com.progressoft.quickpay.payments.repository;

import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import com.progressoft.training.fileparser.repository.TemplateReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TemplateReaderImpl implements TemplateReader {

    private final JsonTemplateRepository jsonTemplateRepository;
    private final TemplateRepositoryAdapter templateRepositoryAdapter;
    private final String defaultTemplateName;

    public TemplateReaderImpl(JsonTemplateRepository jsonTemplateRepository, TemplateRepositoryAdapter templateRepositoryAdapter,
                              @Value("${payments.default-template-name:default}") String defaultTemplateName) {

        this.jsonTemplateRepository = jsonTemplateRepository;
        this.templateRepositoryAdapter = templateRepositoryAdapter;
        this.defaultTemplateName = defaultTemplateName;
    }

    @Override
    public Optional<Template> findByName(String name) {

        if (defaultTemplateName.equals(name)) {
            return jsonTemplateRepository.findByName(name);
        }

        return templateRepositoryAdapter.findByName(name);
    }

    @Override
    public List<Template> findAll() {

        List<Template> templates = new ArrayList<>(jsonTemplateRepository.findAll());
        templates.addAll(templateRepositoryAdapter.findAll());
        return templates;
    }
}