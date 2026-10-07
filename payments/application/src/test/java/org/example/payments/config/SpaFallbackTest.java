package org.example.payments.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Only this tiny controller is loaded, so the test checks just the security rules and the fallback.
@Import({SecurityConfig.class, WebConfig.class, SpaFallbackTest.Dummy.class})
@WebMvcTest(SpaFallbackTest.Dummy.class)
class SpaFallbackTest {

    @RestController
    static class Dummy {
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenPageUrlsWithoutToken_whenGet_thenReturnsIndexHtml() throws Exception {
        for (String url : new String[]{"/payments", "/payments/12", "/payments/new", "/login", "/abc/def"}) {
            mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string("<html>angular</html>\n"));
        }
    }

    @Test
    void givenApiUrlWithoutToken_whenGet_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/payments")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/unknown")).andExpect(status().isUnauthorized());
    }

    @Test
    void givenMissingFile_whenGet_thenNotFoundAndNotIndexHtml() throws Exception {
        mockMvc.perform(get("/missing.js")).andExpect(status().isNotFound());
    }

    @Test
    void isPage_separatesPagesFromFilesAndBackendUrls() {
        assertTrue(SpaPaths.isPage("/payments/5"));
        assertFalse(SpaPaths.isPage("/main-ABC.js"));
        assertFalse(SpaPaths.isPage("/api/payments"));
        assertFalse(SpaPaths.isPage("/swagger-ui/index.html"));
        assertFalse(SpaPaths.isPage("/h2-console"));
    }
}
