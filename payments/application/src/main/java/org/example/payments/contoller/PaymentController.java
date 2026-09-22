package org.example.payments.contoller;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.resource.BulkUploadRequest;
import org.example.payments.resource.BulkUploadResponse;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;
import org.example.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.example.model.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentMapper mapper;
    private final ObjectMapper objectMapper;

    private String DefaultTemplateName;

    public PaymentController(PaymentService service, PaymentMapper mapper,
                             ObjectMapper objectMapper,@Value("${payments.template.default-name}") String DefaultTemplateName) {
        this.service = service;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.DefaultTemplateName = DefaultTemplateName;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        List<PaymentResponse> paymentResponses = service.findAll().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return service.findBy(id).map(domain -> ResponseEntity.ok(mapper.toResponse(domain)))
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping
    public ResponseEntity<PaymentResponse> postPayment(@RequestBody PaymentRequest request) {
        Payment domain = mapper.toDomain(request);
        Payment saved = service.save(domain);
        return new ResponseEntity<>(mapper.toResponse(saved), HttpStatus.CREATED);
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BulkUploadResponse> uploadBulk
            (@RequestPart("file") MultipartFile file,
             @RequestPart(value = "metadata", required = false) String metadata) throws IOException {


        Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());

        try {
            file.transferTo(tempFile);
            String templateName = GetTempFileName(metadata, DefaultTemplateName);
            BulkResult result = service.uploadBulk(templateName, tempFile);
            return new ResponseEntity<>(mapper.toResponse(result), HttpStatus.OK);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private String GetTempFileName(String metadata,  String DefaultTemplateName) {
        if (metadata == null || metadata.isBlank()) {
            return DefaultTemplateName;
        }

        BulkUploadRequest bulkUploadRequest = objectMapper.readValue(metadata, BulkUploadRequest.class);
        return bulkUploadRequest.getTemplateName();
    }
}
