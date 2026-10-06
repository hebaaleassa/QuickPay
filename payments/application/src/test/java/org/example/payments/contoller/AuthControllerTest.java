package org.example.payments.contoller;

import org.example.payments.config.JwtService;
import org.example.payments.config.SecurityConfig;
import org.example.payments.config.TokenVersionStore;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    // ---- login / refresh ----

    private String login(String user, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + user + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private org.springframework.test.web.servlet.ResultActions refresh(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"));
    }

    @Test
    void loginReturnsAccessAndRefreshToken() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"alice123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refreshTokenGivesNewTokens() throws Exception {
        String refreshToken = JsonPath.read(login("alice", "alice123"), "$.refreshToken");

        refresh(refreshToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void accessTokenCannotBeUsedToRefresh() throws Exception {
        String accessToken = JsonPath.read(login("alice", "alice123"), "$.token");

        refresh(accessToken).andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenCannotBeUsedAsAccessToken() throws Exception {
        String refreshToken = JsonPath.read(login("alice", "alice123"), "$.refreshToken");

        mockMvc.perform(get("/api/payments").header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void garbageRefreshTokenIsUnauthorized() throws Exception {
        refresh("not-a-jwt").andExpect(status().isUnauthorized());
    }

    @Test
    void revokedUserCannotRefresh() throws Exception {
        String refreshToken = JsonPath.read(login("alice", "alice123"), "$.refreshToken");

        versions.increment("alice");

        refresh(refreshToken).andExpect(status().isUnauthorized());
    }
}
