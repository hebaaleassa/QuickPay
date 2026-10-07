package org.example.payments.config;

import java.util.List;

// Decides which URLs are Angular pages (answered with index.html) and which belong to the backend.
public final class SpaPaths {

    // URLs that must keep behaving as the backend wants (real data, 401/404 errors).
    private static final List<String> BACKEND_PREFIXES =
            List.of("/api/", "/swagger-ui", "/v3/", "/h2-console", "/webjars/");

    private SpaPaths() {
    }

    // A page URL has no file extension in its last part ("/payments/5", not "/main.js").
    public static boolean isPage(String path) {
        if (BACKEND_PREFIXES.stream().anyMatch(path::startsWith)) {
            return false;
        }
        String lastPart = path.substring(path.lastIndexOf('/') + 1);
        return !lastPart.contains(".");
    }
}
