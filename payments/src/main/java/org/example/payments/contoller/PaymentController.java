package org.example.payments.contoller;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.model.PaymentEntity;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;
import org.example.payments.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;
    private final PaymentMapper mapper;

    public PaymentController(PaymentService service, PaymentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        List<PaymentResponse> paymentResponses = service.findAll().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        Optional<PaymentEntity> payment = service.findBy(id);
        if (!payment.isEmpty())
            return ResponseEntity.ok(mapper.toResponse(payment.get()));
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> postPayment(@RequestBody PaymentRequest request) {
        PaymentEntity payment = mapper.toEntity(request);
        return new ResponseEntity<>(mapper.toResponse(service.save(payment)), HttpStatus.CREATED);
    }

    @GetMapping("/sender/{senderAccount}")
    public ResponseEntity<List<PaymentResponse>> getBySenderAccount(@PathVariable String senderAccount) {

        List<PaymentResponse> paymentResponses = service.findAllBy(senderAccount).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(paymentResponses);
    }
}
