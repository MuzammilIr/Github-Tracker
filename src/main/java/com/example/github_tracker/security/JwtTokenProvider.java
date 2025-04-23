package com.example.github_tracker.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("id", userPrincipal.getId())
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);

        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT Validation Error: " + e.getMessage());
            return false;
        }
    }
}
