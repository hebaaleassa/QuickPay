package com.progressoft.quickpay.payments.specification;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class TemplateSpecification implements Specification<TemplateEntity> {
    private final SearchCriteria criteria;

    public TemplateSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<TemplateEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String field = criteria.field();
        String operation = criteria.operation();
        Object value = criteria.value();
        if (operation.equals("=")) {
            return builder.equal(root.get(field), value);
        }
        if (operation.equals("like")) {
            return builder.like(builder.lower(root.get(field).as(String.class)),
                    "%" + value.toString().toLowerCase() + "%");
        }
        throw new IllegalArgumentException("Unsupported operation: " + operation);
    }
}