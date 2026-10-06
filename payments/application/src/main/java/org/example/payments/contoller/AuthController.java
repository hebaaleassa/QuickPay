package org.example.payments.contoller;

import java.util.Map;

import org.example.payments.config.JwtService;
import org.example.payments.config.TokenVersionStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(String username, String password) {
    }

    public record RefreshRequest(String refreshToken) {
    }

    private final UserDetailsService users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenVersionStore versions;
    private final JwtDecoder refreshDecoder;

    public AuthController(UserDetailsService users, PasswordEncoder passwordEncoder,
                          JwtService jwtService, TokenVersionStore versions,
                          @Qualifier("refreshJwtDecoder") JwtDecoder refreshDecoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.versions = versions;
        this.refreshDecoder = refreshDecoder;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        UserDetails user;
        try {
            user = users.loadUserByUsername(request.username());
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }
        return tokensFor(user);
    }

    // Public endpoint: the refresh token itself is the credential. The decoder rejects expired,
    // wrong-type and revoked (old "ver") tokens, and we answer all of them with the same 401.
    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody RefreshRequest request) {
        UserDetails user;
        try {
            Jwt refreshToken = refreshDecoder.decode(request.refreshToken());
            user = users.loadUserByUsername(refreshToken.getSubject());
        } catch (JwtException | UsernameNotFoundException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        return tokensFor(user);
    }

    private Map<String, String> tokensFor(UserDetails user) {
        return Map.of(
                "token", jwtService.createToken(user),
                "refreshToken", jwtService.createRefreshToken(user));
    }

    @PostMapping("/logout-all")
    public void logoutAll(Authentication authentication) {
        versions.increment(authentication.getName());
    }

    @PostMapping("/revoke/{username}")
    public ResponseEntity<Void> revoke(@PathVariable String username) {
        try {
            users.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        versions.increment(username);
        return ResponseEntity.noContent().build();
    }
}
