package org.example.payments.contoller;

import org.example.payments.model.PaymentEntity;
import org.example.payments.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentMapper paymentMapper;

    private PaymentEntity payment;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setPayment() {
        payment = new PaymentEntity();
        payment.setId(1L);

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(1L);
    }

    @Test
    void givenValidInput_whenFindAllPayment_thenReturnOk() throws Exception {
        when(paymentService.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void givenPaymentPost_whenSavePayment_thenCreated() throws Exception {
        when(paymentMapper.toEntity(any(PaymentRequest.class))).thenReturn(payment);
        when(paymentService.save(any(PaymentEntity.class))).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void givenPaymentId_whenFindPaymentById_thenReturnPaymentStatusOk() throws Exception {
        when(paymentService.findBy(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void givenIdNotFound_whenFindPaymentById_thenReturnNotFound() throws Exception {
        PaymentEntity saved = new PaymentEntity();
        saved.setId(1L);

        when(paymentService.findBy(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/payments/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}