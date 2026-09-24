package com.progressoft.quickpay.payments.resources.template;

import lombok.Data;

import java.util.List;

@Data
public class TemplateRequest {
    private String name;
    private List<TemplateFieldRequest> fields;
}
