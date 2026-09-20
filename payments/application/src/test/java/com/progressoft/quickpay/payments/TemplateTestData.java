package com.progressoft.quickpay.payments;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
import com.progressoft.quickpay.payments.resources.template.TemplateResponse;
import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;

import java.util.List;

public class TemplateTestData {

    public static final Long template_id = 1L;
    public static final String templateName = "default-template";

    public static Template validTemplate() {
        return new Template(templateName, List.of(new FieldDefinition("senderAccount", 34, true)));
    }

    public static TemplateEntity entity() {
        TemplateEntity entity = new TemplateEntity();
        entity.setId(template_id);
        entity.setName(templateName);
        return entity;
    }

    public static TemplateResponse response() {
        TemplateResponse response = new TemplateResponse();
        response.setName(templateName);
        return response;
    }

    public static String validJson() {
        return """
                {
                  "name": "default-template",
                  "fields": [{"name": "senderAccount","length": 34,"required": true}]
                }
                """;
    }
}