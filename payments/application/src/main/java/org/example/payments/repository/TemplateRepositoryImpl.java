package org.example.payments.repository;

import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.TemplateNotFoundException;
import com.progressoft.training.fileparser.repository.TemplateReader;
import com.progressoft.training.fileparser.repository.TemplateRepositoryInterface;
import com.progressoft.training.fileparser.repository.TemplateWriter;
import jakarta.transaction.Transactional;
import org.example.payments.mapper.TemplateMapper;
import org.example.payments.model.TemplateEntity;
import org.example.payments.model.TemplateField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("databaseTemplateRepository")
public class TemplateRepositoryImpl implements TemplateRepositoryInterface {

    private final TempateJpaRepository templateJpaRepository;
    private final TemplateMapper mapper;

    public TemplateRepositoryImpl(TempateJpaRepository templateJpaRepository, TemplateMapper mapper) {
        this.templateJpaRepository = templateJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Template> findByName(String name) {
        return templateJpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Template> findAll() {
        return templateJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    public Optional<Template> findBy(Long id) {
        return templateJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Template save(Template template) {
        TemplateEntity entity = templateJpaRepository.findByName(template.name())
                .map(exists ->{updateFields(exists, template); return exists;})
                .orElseGet(()->mapper.toEntity(template));
        TemplateEntity saved = templateJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    private void updateFields(TemplateEntity entity, Template template) {
        entity.setName(template.name());
        entity.getFields().clear();
        List<TemplateField> entities = mapper.toEntity(template.fields());
        entity.getFields().addAll(entities);
    }

    @Override
    public void deleteByName(String name) {
        TemplateEntity entity = templateJpaRepository.findByName(name).
                orElseThrow(() -> new TemplateNotFoundException(name));
        templateJpaRepository.delete(entity);
    }
}
