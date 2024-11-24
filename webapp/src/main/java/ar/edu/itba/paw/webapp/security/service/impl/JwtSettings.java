package ar.edu.itba.paw.webapp.security.service.impl;

import ar.edu.itba.paw.webapp.security.api.model.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Settings for signing and verifying JWT tokens.
 */
@Component
class JwtSettings {

    /**
     * Secret for signing and verifying the token signature.
     */
    @Value("classpath:jwt.key")
    private String secret;

    /**
     * Allowed clock skew for verifying the token signature (in seconds).
     */
    @Value("300")
    private Long clockSkew;

    /**
     * Identifies the recipients that the JWT token is intended for.
     */
    @Value("${jwt.audience}")
    private String audience;

    /**
     * Identifies the JWT token issuer.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * JWT claim for the authorities.
     */
    @Value("${jwt.authorityClaim}")
    private String authoritiesClaimName;

    /**
     * JWT claim for the token refreshment count.
     */
    @Value("${jwt.tokenType}")
    private String tokenType;

    public String getSecret() {
        return secret;
    }

    public Long getClockSkew() {
        return clockSkew;
    }

    public String getAudience() {
        return audience;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAuthoritiesClaimName() {
        return authoritiesClaimName;
    }

    public String getTokenTypeClaimName() {
        return tokenType;
    }
}
