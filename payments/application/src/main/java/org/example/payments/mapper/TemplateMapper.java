package org.example.payments.mapper;


import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import org.example.payments.model.TemplateEntity;
import org.example.payments.model.TemplateField;
import org.example.payments.resource.TemplateFieldRequest;
import org.example.payments.resource.TemplateFieldResponse;
import org.example.payments.resource.TemplateRequest;
import org.example.payments.resource.TemplateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    @Mapping(source = "requestList", target = "fields")
    Template toDomain(TemplateRequest request);
    Template toDomain(TemplateEntity templateEntity);


    @Mapping(source = "fields", target = "requestList")
    TemplateResponse toResponse(Template template);

    TemplateEntity toEntity(Template template);
    List<TemplateField> toEntity(List<FieldDefinition> fieldDefinition);

}
