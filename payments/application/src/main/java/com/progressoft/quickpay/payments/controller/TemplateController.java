package com.progressoft.quickpay.payments.controller;

import com.progressoft.quickpay.payments.mapper.TemplateMapper;
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
    public List<TemplateResponse> getAll() {
        return service.findAll().stream().map(mapper::toResponse).toList();
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