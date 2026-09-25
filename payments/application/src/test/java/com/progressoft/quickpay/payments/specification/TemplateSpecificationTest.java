package com.progressoft.quickpay.payments.specification;

import com.progressoft.quickpay.payments.entity.TemplateEntity;
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

// Techniques: equivalence partitioning on the operation (=, like, unsupported)
// and error guessing on LIKE wildcards (%, _ and the escape character itself)
@ExtendWith(MockitoExtension.class)
class TemplateSpecificationTest {

    @Mock
    private Root<TemplateEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder builder;
    @Mock
    private Predicate predicate;

    @Test
    void givenEqualsOperationOnId_whenToPredicate_thenEqualPredicate() {
        Path<Object> id = Mockito.mock(Path.class);
        Mockito.doReturn(id).when(root).get("id");
        Mockito.when(builder.equal(id, 5L)).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("id", "=", 5L).toPredicate(root, query, builder));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(delimiter = '|', value = {
            "Reordered   | %reordered%",
            "delta%x     | %delta\\%x%",
            "gamma_1     | %gamma\\_1%",
            "back\\slash | %back\\\\slash%",
    })
    void givenLikeOperationOnName_whenToPredicate_thenCaseInsensitiveContainsWithEscapedWildcards(String value, String pattern) {
        Path<Object> name = Mockito.mock(Path.class);
        Expression<String> nameAsString = Mockito.mock(Expression.class);
        Expression<String> lowerName = Mockito.mock(Expression.class);
        Mockito.doReturn(name).when(root).get("name");
        Mockito.when(name.as(String.class)).thenReturn(nameAsString);
        Mockito.when(builder.lower(nameAsString)).thenReturn(lowerName);
        Mockito.when(builder.like(lowerName, pattern, '\\')).thenReturn(predicate);

        Assertions.assertSame(predicate, specification("name", "like", value).toPredicate(root, query, builder));
    }

    @Test
    void givenUnsupportedOperation_whenToPredicate_thenThrowIllegalArgumentException() {
        TemplateSpecification specification = specification("name", ">=", "a");

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> specification.toPredicate(root, query, builder));

        Assertions.assertEquals("Unsupported operation: >=", exception.getMessage());
        Mockito.verifyNoInteractions(builder);
    }

    private static TemplateSpecification specification(String field, String operation, Object value) {
        return new TemplateSpecification(new SearchCriteria(field, operation, value));
    }
}
