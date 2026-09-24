package com.progressoft.quickpay.payments.controller;

import com.progressoft.quickpay.payments.TemplateTestData;
import com.progressoft.quickpay.payments.mapper.TemplateMapper;
import com.progressoft.quickpay.payments.resources.template.TemplateRequest;
import com.progressoft.quickpay.payments.security.SecurityConfig;
import com.progressoft.quickpay.payments.service.TemplateService;
import com.progressoft.training.fileparser.domain.Template;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TemplateController.class, SecurityConfig.class})
@WithMockUser(roles = "TEMPLATE")
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateService service;

    @MockitoBean
    private TemplateMapper mapper;

    @Test
    void givenTemplate_whenCreate_thenReturn201() throws Exception {

        Template template = TemplateTestData.validTemplate();
        Mockito.when(mapper.toDomain(Mockito.any(TemplateRequest.class))).thenReturn(template);
        Mockito.when(service.create(template)).thenReturn(template);
        Mockito.when(mapper.toResponse(template)).thenReturn(TemplateTestData.response());
        mockMvc.perform(post("/api/templates").contentType(MediaType.APPLICATION_JSON).content(TemplateTestData.validJson())).andExpect(status().isCreated());
    }

    @Test
    void givenTemplateExists_whenGetByName_thenReturn200() throws Exception {

        Template template = TemplateTestData.validTemplate();
        Mockito.when(service.findByName(TemplateTestData.templateName)).thenReturn(Optional.of(template));
        Mockito.when(mapper.toResponse(template)).thenReturn(TemplateTestData.response());
        mockMvc.perform(get("/api/templates/name/" + TemplateTestData.templateName)).andExpect(status().isOk());
    }

    @Test
    void givenTemplateName_whenDelete_thenReturn204() throws Exception {
        mockMvc.perform(delete("/api/templates/" + TemplateTestData.templateName)).andExpect(status().isNoContent());
        Mockito.verify(service).delete(TemplateTestData.templateName);
    }

    @Test
    void givenValidTemplate_whenUpdate_thenReturn200() throws Exception {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(mapper.toDomain(Mockito.any(TemplateRequest.class))).thenReturn(template);

        Mockito.when(service.update(template)).thenReturn(template);
        Mockito.when(mapper.toResponse(template)).thenReturn(TemplateTestData.response());
        mockMvc.perform(put("/api/templates/" + TemplateTestData.templateName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TemplateTestData.validJson())).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(TemplateTestData.templateName));
        Mockito.verify(service).update(template);
    }

    @Test
    void givenTemplateExists_whenGetbyId_thenReturn200() throws Exception {
        Template template = TemplateTestData.validTemplate();
        Mockito.when(service.findById(TemplateTestData.template_id)).thenReturn(Optional.of(template));
        Mockito.when(mapper.toResponse(template)).thenReturn(TemplateTestData.response());

        mockMvc.perform(get("/api/templates/" + TemplateTestData.template_id)).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(TemplateTestData.templateName));
    }
}