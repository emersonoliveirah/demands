package com.demands.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${api.security.token.secret}")
    private String secretKey;

    public Claims extractClaims(String token) {
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            return Jwts.parserBuilder()
                    .setSigningKey(keyBytes)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("JWT validation failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject(); // Extracts the "sub" claim

    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class); // Extracts the "role" claim
    }

    public String extractGroupId(String token) {
        return extractClaims(token).get("groupId", String.class); // Extracts the "groupId" claim
    }
}