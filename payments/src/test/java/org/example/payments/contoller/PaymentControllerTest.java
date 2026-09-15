package org.example.payments.contoller;

import org.example.payments.model.Payment;
import org.example.payments.service.PaymentService;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void given_whenFindAllPayment_thenReturnOk() throws Exception {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("100"));

        when(paymentService.findAllPayment()).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void givenPaymentPost_whenSavePayment_thenCreated() throws Exception {
        Payment saved = new Payment();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("100.00"));

        Mockito.when(paymentService.SavePayment(Mockito.any())).thenReturn(saved);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));

    }

    @Test
    void givenPaymentId_whenFindPaymentById_thenReturnPayment() throws Exception {
        Payment saved = new Payment();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("100.00"));

        Mockito.when(paymentService.findPaymentById(Mockito.any())).thenReturn(Optional.of(saved));

        mockMvc.perform(get("/api/payments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}