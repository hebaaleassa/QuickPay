package com.progressoft.quickpay.payments.controller;


import com.progressoft.quickpay.payments.PaymentTestData;
import com.progressoft.quickpay.payments.domain.filteration.PaymentFilter;
import com.progressoft.quickpay.payments.domain.model.payment.Payment;
import com.progressoft.quickpay.payments.domain.model.payment.PaymentStatus;
import com.progressoft.quickpay.payments.exception.GlobalExceptionHandler;
import com.progressoft.quickpay.payments.mapper.PaymentMapperImpl;
import com.progressoft.quickpay.payments.paging.PagingOptions;
import com.progressoft.quickpay.payments.paging.PagingResult;
import com.progressoft.quickpay.payments.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({GlobalExceptionHandler.class, PaymentMapperImpl.class})
class PaymentControllerTest {

    private static final Long payment_id = PaymentTestData.PAYMENT_ID;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService service;

    @Test
    void givenValidPayment_whenPost_thenReturn201() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PaymentTestData.validJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.creditorName").value("Tolay Khamis"));
        Mockito.verify(service).create(Mockito.any(Payment.class));
    }

    @Test
    void givenPaymentExists_whenGetById_thenReturn200() throws Exception {
        Mockito.when(service.findBy(payment_id))
                .thenReturn(Optional.of(PaymentTestData.savedPayment(payment_id)));
        mockMvc.perform(get("/api/payments/" + payment_id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(PaymentStatus.PENDING.name()));
    }

    @Test
    void givenPaymentsExist_whenGetAll_thenReturn200() throws Exception {
        Mockito.when(service.findAll(Mockito.any(PaymentFilter.class),
                        Mockito.any(PagingOptions.class),
                        Mockito.eq("id"),
                        Mockito.eq("asc")))
                .thenReturn(new PagingResult<>(List.of(PaymentTestData.savedPayment(payment_id),
                        PaymentTestData.savedPayment(payment_id + 1)), 0, 10, 2, 1));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(payment_id)).andExpect(jsonPath("$.content[1].id").value(payment_id + 1));
    }

    @Test
    void givenPaymentMissing_whenGetById_thenReturn404() throws Exception {
        Mockito.when(service.findBy(payment_id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/payments/" + payment_id))
                .andExpect(status().isNotFound());
    }
}

