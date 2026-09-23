package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.domain.filteration.TemplateFilter;
import com.progressoft.quickpay.payments.paging.PagingOptions;
import com.progressoft.quickpay.payments.paging.PagingResult;
import com.progressoft.quickpay.payments.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.Template;

public interface TemplateSearchRepository {
    PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions,
                                   TemplateSortField sortField, String direction);
}
