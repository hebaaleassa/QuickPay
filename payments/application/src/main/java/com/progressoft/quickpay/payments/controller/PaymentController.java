package com.progressoft.quickpay.payments.controller;


import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.mapper.PaymentMapper;
import com.progressoft.quickpay.payments.resources.PaymentRequest;
import com.progressoft.quickpay.payments.resources.PaymentResponse;
import com.progressoft.quickpay.payments.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.info("recieved request to create payment");
        Payment payment = mapper.toDomain(mapper.toRequest(request));
        service.create(payment);
        log.info("payment created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getOne(@PathVariable Long id) {
        log.info("recieved request for one payment");
        Payment payment = service.findBy(id).orElse(null);
        if (payment == null) {
            log.warn("Payment not found");
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return service.findAll().stream().map(mapper::toResponse).toList();
    }
}