package com.progressoft.quickpay.payments.resources.paging;

import java.util.List;

public record PagingResult<T>(List<T> content, int pageNumber,
                              int pageSize, long totalElements,
                              int totalPages) {
}
