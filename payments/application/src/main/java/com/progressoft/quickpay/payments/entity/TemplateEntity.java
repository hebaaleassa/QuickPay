package com.progressoft.quickpay.payments.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "templates")
@Setter
@Getter
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_sequence")
    @SequenceGenerator(
            name = "template_sequence",
            sequenceName = "templates_sequence",
            allocationSize = 1
    )
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_fields", joinColumns = @JoinColumn(name = "template_id"))
    @OrderColumn(name = "field_order")
    private List<TemplateFieldEntity> fields = new ArrayList<>();
}