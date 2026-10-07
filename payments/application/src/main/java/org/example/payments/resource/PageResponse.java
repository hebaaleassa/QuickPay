package org.example.payments.resource;

import org.example.model.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <D, R> PageResponse<R> of(PageResult<D> result, Function<D, R> toResponse) {
        List<R> content = result.content().stream().map(toResponse).toList();
        int totalPages = (int) Math.ceil((double) result.totalElements() / result.size());
        return new PageResponse<>(content, result.page(), result.size(), result.totalElements(), totalPages);
    }
}
