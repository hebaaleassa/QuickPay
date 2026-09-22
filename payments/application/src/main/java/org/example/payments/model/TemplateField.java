package org.example.payments.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class TemplateField {
    @Column(name = "fields_name")
    private String name;

    @Column(name = "fields_length")
    private int length;

    @Column(name = "required")
    private boolean required;

}
