package com.progressoft.quickpay.payments.controller;


import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import com.progressoft.quickpay.payments.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentMapper mapper;
    private final PaymentService service;

    public PaymentController(PaymentMapper mapper, PaymentService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        PaymentEntity saved = service.create(mapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getOne(@PathVariable Long id) {
        PaymentEntity payment = service.findBy(id).orElse(null);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return service.findAll().stream().map(mapper::toResponse).toList();
    }
}