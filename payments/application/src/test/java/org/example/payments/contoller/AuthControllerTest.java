package org.example.payments.contoller;

import org.example.payments.config.JwtService;
import org.example.payments.config.SecurityConfig;
import org.example.payments.config.TokenVersionStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtService.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenVersionStore versions;

    @Test
    @WithMockUser(username = "admin", roles = {"TEMPLATE", "ADMIN"})
    void adminCanRevokeAnotherUser() throws Exception {
        mockMvc.perform(post("/api/auth/revoke/alice")).andExpect(status().isNoContent());

        assertEquals(2, versions.getVersion("alice"));
        assertEquals(1, versions.getVersion("admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"TEMPLATE", "ADMIN"})
    void revokingUnknownUserReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/auth/revoke/nobody")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "alice", roles = "PAYMENT")
    void normalUserCannotRevoke() throws Exception {
        mockMvc.perform(post("/api/auth/revoke/admin")).andExpect(status().isForbidden());

        assertEquals(1, versions.getVersion("admin"));
    }

    @Test
    void anonymousCannotRevoke() throws Exception {
        mockMvc.perform(post("/api/auth/revoke/alice")).andExpect(status().isUnauthorized());
    }
}
