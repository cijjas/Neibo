package ar.edu.itba.paw.webapp.security.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class JwtSettings {

    @Value("classpath:jwt.key")
    private String secret;

    @Value("300")
    private Long clockSkew;

    @Value("${jwt.audience}")
    private String audience;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.authorityClaim}")
    private String authoritiesClaimName;

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
