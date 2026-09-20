package com.progressoft.quickpay.payments.resources.template;

import lombok.Data;

import java.util.List;

@Data
public class TemplateResponse {
    private String name;
    private List<TemplateFieldResponse> fields;
}
