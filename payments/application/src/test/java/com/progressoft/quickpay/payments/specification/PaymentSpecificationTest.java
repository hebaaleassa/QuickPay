package com.progressoft.quickpay.payments.specification;

import com.progressoft.quickpay.payments.entity.PaymentEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

// Techniques: equivalence partitioning on the operation (=, like, >=, <=, unsupported)
// and error guessing on LIKE wildcards (%, _ and the escape character itself)
@ExtendWith(MockitoExtension.class)
class PaymentSpecificationTest {

    @Mock
    private Root<PaymentEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder builder;
    @Mock
    private Predicate predicate;

    @Test
    void givenEqualsOperation_whenToPredicate_thenEqualPredicateOnTheField() {
        Path<Object> currency = Mockito.mock(Path.class);
        Mockito.doReturn(currency).when(root).get("currency");
        Mockito.when(builder.equal(currency, "USD")).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("currency", "=", "USD").toPredicate(root, query, builder));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(delimiter = '|', value = {
            "ACC-1      | %acc-1%",
            "100%       | %100\\%%",
            "SEND_1     | %send\\_1%",
            "back\\slash | %back\\\\slash%",
    })
    void givenLikeOperation_whenToPredicate_thenCaseInsensitiveContainsWithEscapedWildcards(String value, String pattern) {
        Path<Object> sender = Mockito.mock(Path.class);
        Expression<String> senderAsString = Mockito.mock(Expression.class);
        Expression<String> lowerSender = Mockito.mock(Expression.class);
        Mockito.doReturn(sender).when(root).get("senderAccount");
        Mockito.when(sender.as(String.class)).thenReturn(senderAsString);
        Mockito.when(builder.lower(senderAsString)).thenReturn(lowerSender);
        Mockito.when(builder.like(lowerSender, pattern, '\\')).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("senderAccount", "like", value).toPredicate(root, query, builder));
    }

    @Test
    void givenGreaterOrEqualOnAmount_whenToPredicate_thenGreaterThanOrEqualPredicate() {
        Path<BigDecimal> amount = Mockito.mock(Path.class);
        BigDecimal minAmount = new BigDecimal("10.00");
        Mockito.doReturn(amount).when(root).get("amount");
        Mockito.when(builder.greaterThanOrEqualTo(amount, minAmount)).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("amount", ">=", minAmount).toPredicate(root, query, builder));
    }

    @Test
    void givenLessOrEqualOnAmount_whenToPredicate_thenLessThanOrEqualPredicate() {
        Path<BigDecimal> amount = Mockito.mock(Path.class);
        BigDecimal maxAmount = new BigDecimal("500");
        Mockito.doReturn(amount).when(root).get("amount");
        Mockito.when(builder.lessThanOrEqualTo(amount, maxAmount)).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("amount", "<=", maxAmount).toPredicate(root, query, builder));
    }

    @Test
    void givenRangeOnCreatedAt_whenToPredicate_thenInstantsAreComparedToo() {
        Path<Instant> createdAt = Mockito.mock(Path.class);
        Instant from = Instant.parse("2026-09-25T00:00:00Z");
        Mockito.doReturn(createdAt).when(root).get("createdAt");
        Mockito.when(builder.greaterThanOrEqualTo(createdAt, from)).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("createdAt", ">=", from).toPredicate(root, query, builder));
    }

    @Test
    void givenUnsupportedOperation_whenToPredicate_thenThrowIllegalArgumentException() {
        PaymentSpecification specification = specification("amount", "!=", BigDecimal.ONE);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> specification.toPredicate(root, query, builder));

        Assertions.assertEquals("Unsupported operation: !=", exception.getMessage());
        Mockito.verifyNoInteractions(builder);
    }

    private static PaymentSpecification specification(String field, String operation, Object value) {
        return new PaymentSpecification(new SearchCriteria(field, operation, value));
    }
}
