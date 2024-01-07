package ar.edu.itba.paw.webapp.security.service.impl;

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
    @Value("YourSecretKeyHere")
    private String secret;

    /**
     * Allowed clock skew for verifying the token signature (in seconds).
     */
    @Value("300")
    private Long clockSkew;

    /**
     * Identifies the recipients that the JWT token is intended for.
     */
    @Value("your-audience")
    private String audience;

    /**
     * Identifies the JWT token issuer.
     */
    @Value("your-issuer")
    private String issuer;

    /**
     * JWT claim for the authorities.
     */
    @Value("your-authorities-claim")
    private String authoritiesClaimName;

    /**
     * JWT claim for the token refreshment count.
     */
    @Value("your-refresh-count-claim")
    private String refreshCountClaimName;

    /**
     * JWT claim for the maximum times that a token can be refreshed.
     */
    @Value("your-refresh-limit-claim")
    private String refreshLimitClaimName;

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

    public String getRefreshCountClaimName() {
        return refreshCountClaimName;
    }

    public String getRefreshLimitClaimName() {
        return refreshLimitClaimName;
    }
}
