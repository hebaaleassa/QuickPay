package com.progressoft.quickpay.payments.paging;

import com.progressoft.quickpay.payments.domain.exception.InvalidPagingException;

public record PagingOptions(int pageNumber, int pageSize) {
    public static final int MAX_PAGE_SIZE = 100;

    public PagingOptions {
        if (pageNumber < 0) {
            throw new InvalidPagingException("Page number must be higher or equals to zero");
        }
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new InvalidPagingException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }
}
