package org.example.payments.contoller;

import org.example.payments.model.Payment;
import org.example.payments.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getPayments() {
        return ResponseEntity.ok(service.findAllPayment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
            return ResponseEntity.of(service.findPaymentById(id));
    }

    @PostMapping
    public ResponseEntity<Payment> postPayment(@RequestBody Payment payment) {
        return new ResponseEntity<>(service.SavePayment(payment), HttpStatus.CREATED);
    }

    @GetMapping("/sender/{senderAccount}")
    public ResponseEntity<List<Payment>> getBySenderAccount(@PathVariable String senderAccount) {
        return new ResponseEntity<>(service.findAllBySender(senderAccount), HttpStatus.ACCEPTED);
    }

}
