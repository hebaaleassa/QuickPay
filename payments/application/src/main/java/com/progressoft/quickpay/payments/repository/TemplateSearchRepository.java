package com.progressoft.quickpay.payments.repository;

import com.progressoft.quickpay.payments.repository.models.TemplateFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.sorting.TemplateSortField;
import com.progressoft.training.fileparser.domain.Template;

public interface TemplateSearchRepository {
    PagingResult<Template> findAll(TemplateFilter filter, PagingOptions pagingOptions,
                                   TemplateSortField sortField, String direction);
}
