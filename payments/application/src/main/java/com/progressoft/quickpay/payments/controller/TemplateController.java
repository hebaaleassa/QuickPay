package com.progressoft.quickpay.payments.controller;

import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.quickpay.payments.repository.models.TemplateFilter;
import com.progressoft.quickpay.payments.resources.paging.PagingOptions;
import com.progressoft.quickpay.payments.resources.paging.PagingResult;
import com.progressoft.quickpay.payments.resources.template.TemplateRequest;
import com.progressoft.quickpay.payments.resources.template.TemplateResponse;
import com.progressoft.quickpay.payments.service.TemplateService;
import com.progressoft.training.fileparser.domain.Template;
import com.progressoft.training.fileparser.exception.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;
    private final TemplateMapper mapper;

    public TemplateController(TemplateService service, TemplateMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@RequestBody TemplateRequest request) {

        log.info("received request to create template");
        Template template = mapper.toDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.create(template)));
    }

    @GetMapping
    public PagingResult<TemplateResponse> getAll(@RequestParam(required = false) Long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(defaultValue = "0") int pageNumber,
                                                 @RequestParam(defaultValue = "20") int pageSize,
                                                 @RequestParam(defaultValue = "id") String sortBy,
                                                 @RequestParam(defaultValue = "asc") String direction) {
        TemplateFilter filter = new TemplateFilter(id, name);
        PagingOptions pagingOptions = new PagingOptions(pageNumber, pageSize);
        PagingResult<Template> result = service.findAll(filter, pagingOptions, sortBy, direction);
        List<TemplateResponse> content = result.content().stream().map(mapper::toResponse).toList();
        return new PagingResult<>(content, result.pageNumber(), result.pageSize(), result.totalElements(), result.totalPages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getById(@PathVariable Long id) {

        return service.findById(id).map(mapper::toResponse).map(ResponseEntity::ok).orElseThrow(() -> new TemplateNotFoundException(id.toString()));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TemplateResponse> getByName(@PathVariable String name) {

        return service.findByName(name).map(mapper::toResponse).map(ResponseEntity::ok).orElseThrow(() -> new TemplateNotFoundException(name));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        log.info("received request to delete template");
        service.delete(name);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{name}")
    public ResponseEntity<TemplateResponse> update(@PathVariable String name, @RequestBody TemplateRequest request) {
        request.setName(name);
        log.info("recieved request to update template");
        Template template = mapper.toDomain(request);
        return ResponseEntity.ok(mapper.toResponse(service.update(template)));
    }
}