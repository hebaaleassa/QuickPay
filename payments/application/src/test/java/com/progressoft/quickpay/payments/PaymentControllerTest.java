package com.progressoft.quickpay.payments;


import com.progressoft.quickpay.payments.controller.PaymentController;
import com.progressoft.quickpay.payments.entity.PaymentEntity;
import com.progressoft.quickpay.payments.mapper.PaymentMapperImpl;
import com.progressoft.quickpay.payments.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(PaymentMapperImpl.class)
class PaymentControllerTest {

    private static final Long payment_id = 100L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService service;

    @Test
    void givenValidPayment_whenPost_thenReturn201() throws Exception {
        PaymentEntity saved = new PaymentEntity();
        saved.setId(payment_id);
        saved.setAmount(new BigDecimal("100.00"));
        Mockito.when(service.create(Mockito.any())).thenReturn(saved);
        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content("{\"amount\": 100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(payment_id));
    }

    @Test
    void givenPaymentExists_whenGetById_thenReturn200() throws Exception {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(payment_id);
        Mockito.when(service.findBy(payment_id)).thenReturn(Optional.of(payment));
        mockMvc.perform(get("/api/payments/" + payment_id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment_id));
    }

    @Test
    void givenPaymentsExist_whenGetAll_thenReturn200() throws Exception {
        PaymentEntity payment = new PaymentEntity();
        payment.setId(payment_id);
        Mockito.when(service.findAll()).thenReturn(List.of(payment));
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(payment_id));
    }
}

