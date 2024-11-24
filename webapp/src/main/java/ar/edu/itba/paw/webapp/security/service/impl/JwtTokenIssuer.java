package ar.edu.itba.paw.webapp.security.service.impl;

import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenIssuer {

    @Autowired
    private JwtSettings settings;

    public String issueToken(AuthenticationTokenDetails authenticationTokenDetails) {

        return Jwts.builder()
                .setId(authenticationTokenDetails.getId())
                .setIssuer(settings.getIssuer())
                .setAudience(settings.getAudience())
                .setSubject(authenticationTokenDetails.getUsername())
                .setIssuedAt(Date.from(authenticationTokenDetails.getIssuedDate().toInstant()))
                .setExpiration(Date.from(authenticationTokenDetails.getExpirationDate().toInstant()))
                .claim(settings.getAuthoritiesClaimName(), authenticationTokenDetails.getAuthorities())
                .claim(settings.getTokenTypeClaimName(), authenticationTokenDetails.getTokenType())
                .signWith(SignatureAlgorithm.HS256, settings.getSecret())
                .compact();
    }
}
