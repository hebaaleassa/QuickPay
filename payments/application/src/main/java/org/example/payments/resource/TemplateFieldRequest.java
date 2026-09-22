package org.example.payments.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFieldRequest {
    private String name;
    private int length;
    private boolean required;
}

