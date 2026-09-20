package com.progressoft.quickpay.payments.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class TemplateFieldEntity {
    private String name;
    private int length;
    private boolean required;
}
