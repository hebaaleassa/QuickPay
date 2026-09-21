package org.example.payments.contoller;

import org.example.payments.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.example.payments.mapper.PaymentMapper;
import org.example.payments.resource.PaymentRequest;
import org.example.payments.resource.PaymentResponse;

import static org.mockito.ArgumentMatchers.any;
import org.example.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentMapper paymentMapper;

    @MockitoBean
    private ObjectMapper objectMapper;

    private Payment payment;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(BigDecimal.valueOf(555));
        payment.setCurrency("USD");

        paymentResponse = new PaymentResponse();
        paymentResponse.setId(1L);
        paymentResponse.setAmount(BigDecimal.valueOf(100));
        paymentResponse.setCurrency("USD");
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
    void givenValidId_whenFindById_thenReturnOk() throws Exception {
        when(paymentService.findBy(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void givenValidRequest_whenCreatePayment_thenReturnCreated() throws Exception {
        String jsonFile = "{\"senderAccount\":\"ACC1004\",\"receiverAccount\":\"ACC2004\",\"amount\":250.50,\"currency\":\"USD\",\"notes\":\"\",\"creditorName\":\"Heba Aleassa\"}";

        when(paymentMapper.toDomain(any(PaymentRequest.class))).thenReturn(payment);
        when(paymentService.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

//    @Test
//    void givenValidRequest_whenUploadPayment_thenReturnOk() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("file",
//                "text.csv", MediaType.TEXT_EVENT_STREAM_VALUE,
//                "amount,currency\n522,USD".getBytes());
//
//
//
//
//    }
}