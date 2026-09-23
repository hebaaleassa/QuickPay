package com.progressoft.quickpay.payments.specification;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PaymentSpecification implements Specification<PaymentEntity> {
    private final SearchCriteria criteria;

    public PaymentSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    @Override
    public Predicate toPredicate(Root<PaymentEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String field = criteria.field();
        String operation = criteria.operation();

        if (operation.equals("=")) {
            return builder.equal(root.get(field), criteria.value());
        }
        if (operation.equals("like")) {
            String value = escapeLike(criteria.value().toString().toLowerCase());
            return builder.like(builder.lower(root.get(field).as(String.class)), "%" + value + "%", '\\');
        }
        Expression<BigDecimal> fieldValue = root.get(field);
        BigDecimal value = (BigDecimal) criteria.value();
        if (operation.equals(">=")) {
            return builder.greaterThanOrEqualTo(fieldValue, value);
        }
        if (operation.equals("<=")) {
            return builder.lessThanOrEqualTo(fieldValue, value);
        }

        throw new IllegalArgumentException("Unsupported operation: " + operation);
    }
}