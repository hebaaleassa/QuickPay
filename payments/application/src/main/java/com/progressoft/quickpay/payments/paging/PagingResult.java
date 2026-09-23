package com.progressoft.quickpay.payments.paging;

import java.util.List;

public record PagingResult<T>(List<T> content, int pageNumber,
                              int pageSize, long totalElements,
                              int totalPages) {
}
