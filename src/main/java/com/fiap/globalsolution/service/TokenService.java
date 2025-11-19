package com.fiap.globalsolution.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fiap.globalsolution.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service responsible for generating and validating JWT tokens.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private static final String ISSUER = "pathfinder-ai-api";
    private static final ZoneOffset SAO_PAULO_ZONE_OFFSET = ZoneOffset.of("-03:00");

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private long expirationHours;

    /**
     * Generates a JWT token for a given user.
     *
     * @param usuario The user to generate the token for.
     * @return The generated JWT token as a String.
     * @throws RuntimeException if an error occurs during token creation.
     */
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Error while generating token for user {}", usuario.getEmail(), e);
            throw new RuntimeException("Error while generating token", e);
        }
    }

    /**
     * Validates a JWT token and returns the subject (user's email).
     *
     * @param token The JWT token to validate.
     * @return The subject of the token if valid, otherwise returns null.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.warn("Invalid JWT token received: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generates the expiration date for the token based on the configured expiration hours.
     * @return The expiration date as an Instant.
     */
    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(expirationHours).toInstant(SAO_PAULO_ZONE_OFFSET);
    }
}
