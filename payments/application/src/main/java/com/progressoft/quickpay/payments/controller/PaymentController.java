package com.progressoft.quickpay.payments.controller;


import com.progressoft.quickpay.payments.domain.exception.PaymentNotFoundException;
import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.paging.PagingOptions;
import com.progressoft.quickpay.payments.domain.paging.PagingResult;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import com.progressoft.quickpay.payments.resources.BulkUploadRequest;
import com.progressoft.quickpay.payments.resources.BulkUploadResultResponse;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import com.progressoft.quickpay.payments.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentMapper mapper;
    private final PaymentService service;
    private final ObjectMapper objectMapper;
    @Value("${payments.default-template-name:default}")
    private String defaultTemplate;

    public PaymentController(PaymentMapper mapper, PaymentService service, ObjectMapper objectMapper) {
        this.mapper = mapper;
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        log.info("recieved request to create payment");
        Payment payment = mapper.toDomain(mapper.toRequest(request));
        service.create(payment);
        log.info("payment created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getOne(@PathVariable Long id) {
        log.info("recieved request for one payment");
        Payment payment = service.findBy(id).orElseThrow(() -> new PaymentNotFoundException(id));

        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping
    public PagingResult<PaymentResponse> getAll(@ModelAttribute PaymentFilter filter,
                                                @RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "2") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortBy,
                                                @RequestParam(defaultValue = "asc") String direction) {

        PagingOptions pagingOptions = new PagingOptions(pageNumber, pageSize);
        PagingResult<Payment> result = service.findAll(filter, pagingOptions, sortBy, direction);
        List<PaymentResponse> content = result.content().stream().map(mapper::toResponse).toList();
        return new PagingResult<>(content, result.pageNumber(), result.pageSize(), result.totalElements(), result.totalPages());
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkUploadResultResponse> uploadBulk(@RequestPart("file") MultipartFile file,
                                                               @RequestPart(value = "metadata", required = false)
                                                               String metadataJson) throws IOException {
        log.info("received request to upload bulk payments");
        String templateName = readTemplateName(metadataJson);
        Path tempFile = copyFile(file);
        try {
            return ResponseEntity.ok(mapper.toResponse(service.uploadBulk(templateName, tempFile)));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private Path copyFile(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    private String readTemplateName(String json) throws IOException {
        if (json == null || json.isBlank()) {
            return defaultTemplate;
        }
        BulkUploadRequest request = objectMapper.readValue(json, BulkUploadRequest.class);
        return request.getTemplateName();
    }
}