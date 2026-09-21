package com.progressoft.quickpay.payments.mapper;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.entity.TemplateFieldEntity;
import com.progressoft.quickpay.payments.resources.template.TemplateRequest;
import com.progressoft.quickpay.payments.resources.template.TemplateResponse;
import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    Template toDomain(TemplateEntity entity);

    TemplateEntity toEntity(Template template);

    List<TemplateFieldEntity> toEmbeddable(List<FieldDefinition> fields);

    Template toDomain(TemplateRequest request);

    TemplateResponse toResponse(Template template);
}