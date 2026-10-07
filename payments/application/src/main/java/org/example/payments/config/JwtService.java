package org.example.payments.config;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final JwtEncoder encoder;
    private final TokenVersionStore versions;
    private final long expiryMinutes;
    private final long refreshExpiryMinutes;

    public JwtService(JwtEncoder encoder, TokenVersionStore versions,
                      @Value("${jwt.expiry-minutes}") long expiryMinutes,
                      @Value("${jwt.refresh-expiry-minutes}") long refreshExpiryMinutes) {
        this.encoder = encoder;
        this.versions = versions;
        this.expiryMinutes = expiryMinutes;
        this.refreshExpiryMinutes = refreshExpiryMinutes;
    }

    // Short-lived token sent on every API call. "type" stops it being used as a refresh token.
    public String createToken(UserDetails user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .toList();

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiryMinutes * 60))
                .claim("roles", roles)
                .claim("ver", versions.getVersion(user.getUsername()))
                .claim("type", ACCESS)
                .build();
        return encode(claims);
    }

    // Long-lived token, only ever sent to /api/auth/refresh. No roles: they are read again from the
    // user on refresh, so a role change takes effect at the next refresh.
    public String createRefreshToken(UserDetails user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpiryMinutes * 60))
                .claim("ver", versions.getVersion(user.getUsername()))
                .claim("type", REFRESH)
                .build();
        return encode(claims);
    }

    private String encode(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
