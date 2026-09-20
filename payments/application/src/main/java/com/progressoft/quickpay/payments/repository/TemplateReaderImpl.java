package com.progressoft.quickpay.payments.repository;

import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import com.progressoft.training.fileparser.repository.TemplateReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TemplateReaderImpl implements TemplateReader {

    private final JsonTemplateRepository jsonTemplateRepository;
    private final TemplateRepositoryImpl templateRepository;

    public TemplateReaderImpl(JsonTemplateRepository jsonTemplateRepository, TemplateRepositoryImpl templateRepository) {

        this.jsonTemplateRepository = jsonTemplateRepository;
        this.templateRepository = templateRepository;
    }

    @Override
    public Optional<Template> findByName(String name) {

        if ("default".equals(name)) {
            return jsonTemplateRepository.findByName(name);
        }

        return templateRepository.findByName(name);
    }

    @Override
    public List<Template> findAll() {

        List<Template> templates = new ArrayList<>(jsonTemplateRepository.findAll());
        templates.addAll(templateRepository.findAll());
        return templates;
    }
}