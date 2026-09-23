package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.filteration.TemplateFilter;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;
import com.progressoft.quickpay.payments.domain.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.Template;

public interface TemplateSearchRepository {
    PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions,
                                   TemplateSortField sortField, String direction);
}
