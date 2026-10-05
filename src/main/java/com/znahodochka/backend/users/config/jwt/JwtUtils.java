package com.znahodochka.backend.users.config.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.znahodochka.backend.users.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtKeyService jwtKeyService;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(Objects.requireNonNull(userDetails).getUsername())
                .claim("id", userDetails.getID())
                .claim("roles", authentication.getAuthorities().stream().map(
                    GrantedAuthority::getAuthority
                ).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtKeyService.getSigningKey())
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtKeyService.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (final MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (final ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (final UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (final IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public List<String> getAuthoritiesFromToken(final String jwt) {
        try {
            final var rolesObject = Jwts.parser()
                    .verifyWith(jwtKeyService.getSigningKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .get("roles");

            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(rolesObject, new TypeReference<>() {});
        } catch (final ExpiredJwtException e) {
            final var rolesObject = e.getClaims().get("roles");
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(rolesObject, new TypeReference<>() {});
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to deserialize roles claim", e);
        }
    }

    public Long getUserIdFromToken(final String jwt) {
        try {
            final var idObject = Jwts.parser()
                    .verifyWith(jwtKeyService.getSigningKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .get("id");

            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(idObject, Long.class);
        } catch (final ExpiredJwtException e) {
            final var idObject = e.getClaims().get("id");
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(idObject, Long.class);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to deserialize ID claim", e);
        }
    }
}
