package org.example.payments.contoller;

import com.progressoft.training.fileparser.domain.FieldDefinition;
import com.progressoft.training.fileparser.domain.Template;
import org.example.payments.mapper.TemplateMapper;
import org.example.payments.resource.TemplateFieldResponse;
import org.example.payments.resource.TemplateRequest;
import org.example.payments.resource.TemplateResponse;
import org.example.payments.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TemplateService templateService;

    @MockitoBean
    private TemplateMapper mapper;

    Template template = new Template("payment-default", List.of(
            new FieldDefinition("amount", 20, true)));

    TemplateResponse response;

    String requestBody;
    @BeforeEach
    void setUp() {

        response = TemplateResponse.builder()
                .name("payment-default")
                .requestList(List.of(
                        TemplateFieldResponse.builder()
                                .name("amount").length(20).required(true).build())).build();


         requestBody = """
                {
                  "name": "payment-default",
                  "requestList": [
                    {
                      "name": "amount",
                      "length": 20,
                      "required": true
                    }
                  ]
                }
                """;

    }

    @Test
    void givenValidRequest_whenCreateTemplate_thenReturnCreated()
            throws Exception {

        when(mapper.toDomain(any(TemplateRequest.class)))
                .thenReturn(template);

        when(templateService.create(template))
                .thenReturn(template);

        when(mapper.toResponse(template))
                .thenReturn(response);

        mockMvc.perform(post("/api/templates")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("payment-default"));

        verify(templateService).create(template);
    }


    @Test
    public void givenTemplateName_whenGetTemplate_thenReturnOk()
            throws Exception {

        String name = "payment-default";
        when(templateService.findByName(template.name())).thenReturn(Optional.of(template));
        when(mapper.toResponse(template)).thenReturn((response));

        mockMvc.perform(get("/api/templates/by-name/{name}", template.name())).andExpect(status().isOk());


    }

    @Test
    public void givenInvalidTemplateName_whenGetTemplate_thenReturnNoyFound()
            throws Exception {

        String name = "notFound";
        when(templateService.findByName(name)).thenReturn(Optional.empty());
        when(mapper.toResponse(template)).thenReturn((response));

        mockMvc.perform(get("/api/templates/by-name/{name}", template.name())).andExpect(status().isNotFound());


    }

    @Test
    public void givenId_whenGetTemplate_thenReturnOk()
            throws Exception {
        Long id = 2L;
        when(templateService.findBy(id)).thenReturn(Optional.of(template));
        when(mapper.toResponse(template)).thenReturn((response));

        mockMvc.perform(get("/api/templates/{id}", id)).andExpect(status().isOk());
    }

    @Test
    public void givenInvalidId_whenGetTemplate_thenReturnNotFound()
            throws Exception {
        Long id = 2L;
        when(templateService.findBy(id)).thenReturn(Optional.empty());
        when(mapper.toResponse(template)).thenReturn((response));

        mockMvc.perform(get("/api/templates/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    public void givenValidRequest_whenUpdateTemplate_thenReturnUpdated()
            throws Exception {
        String name = "payment-default";
        when(mapper.toDomain(any(TemplateRequest.class))).thenReturn(template);
        when(templateService.update(template)).thenReturn(template);
        when(mapper.toResponse(template)).thenReturn((response));

        mockMvc.perform(put("/api/templates/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void giveName_whenDeleteTemplate_thenReturnDeleted()
            throws Exception {
        String name = "payment-default";
        mockMvc.perform(delete("/api/templates/{name}", name)).
                andExpect(status().isNoContent());
        verify(templateService).delete(name);
    }

    @Test
    public void givenValidRequest_whenGetAll_thenReturnOk()
            throws Exception {

        when(templateService.findAll()).thenReturn(List.of(template));
        when(mapper.toResponse(template)).thenReturn((response));
        mockMvc.perform(get("/api/templates")).andExpect(status().isOk());
    }


}