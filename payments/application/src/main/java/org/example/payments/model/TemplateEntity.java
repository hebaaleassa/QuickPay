package org.example.payments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "templates")
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_sequence_gen")
    @SequenceGenerator(name = "template_sequence_gen"
    , sequenceName = "template_sequence", allocationSize = 1)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_fields", joinColumns = @JoinColumn(name = "template_id"))

    @OrderColumn(name = "fields_order")
    private List<TemplateField> fields = new ArrayList<>();

}
