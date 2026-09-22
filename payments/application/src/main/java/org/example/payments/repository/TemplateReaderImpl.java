package org.example.payments.repository;

import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.repository.JsonTemplateRepository;
import com.progressoft.training.fileparser.repository.TemplateReader;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

//@Primary
@Component
public class TemplateReaderImpl implements TemplateReader {

    private final TemplateRepositoryImpl repository;
    private final JsonTemplateRepository jsonTemplateRepository;

    public TemplateReaderImpl(TemplateRepositoryImpl repository, JsonTemplateRepository jsonTemplateRepository) {
        this.repository = repository;
        this.jsonTemplateRepository = jsonTemplateRepository;
    }

    @Override
    public List<Template> findAll() {
        List<Template> templates = jsonTemplateRepository.findAll();
        templates.addAll(repository.findAll());
        return templates;
    }

    @Override
    public Optional<Template> findByName(String name) {
        if (name == null || name.isBlank() || "payment-default".equals(name)) {
            return jsonTemplateRepository.findByName("payment-default");
        }
        return repository.findByName(name);
    }
}

