package ar.edu.itba.paw.webapp.security.service.impl;


import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.enums.TokenType;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class DefaultAuthenticationTokenService implements AuthenticationTokenService {

    @Value("3600") // 60 minutes
    private Long accessTokenValidity; // How long the access token is valid for, in seconds

    @Value("604800") // 7 days
    private Long refreshTokenValidity; // How long the refresh token is valid for, in seconds

    @Autowired
    private JwtTokenIssuer tokenIssuer;

    @Autowired
    private JwtTokenParser tokenParser;

    @Override
    public String issueAccessToken(String username, Set<Authority> authorities) {
        ZonedDateTime issuedDate = ZonedDateTime.now();
        AuthenticationTokenDetails authenticationTokenDetails = new AuthenticationTokenDetails.Builder()
                .withId(generateTokenIdentifier())
                .withUsername(username)
                .withAuthorities(authorities)
                .withIssuedDate(issuedDate)
                .withExpirationDate(issuedDate.plusSeconds(accessTokenValidity))
                .withTokenType(TokenType.ACCESS)
                .build();

        return tokenIssuer.issueToken(authenticationTokenDetails);
    }

    /*
     * Improvements:
     * - The information stored in the Refresh Token should be minimized, limiting only to userId ideally
     * - The Refresh Token should be stored in the DB, this would allow for Refreshing Limit, and for Token Invalidation
     * */
    @Override
    public String issueRefreshToken(String username, Set<Authority> authorities) {
        ZonedDateTime issuedDate = ZonedDateTime.now();
        AuthenticationTokenDetails authenticationTokenDetails = new AuthenticationTokenDetails.Builder()
                .withId(generateTokenIdentifier())
                .withUsername(username)
                .withAuthorities(authorities)
                .withIssuedDate(issuedDate)
                .withExpirationDate(issuedDate.plusSeconds(refreshTokenValidity))
                .withTokenType(TokenType.REFRESH)
                .build();

        return tokenIssuer.issueToken(authenticationTokenDetails);
    }

    @Override
    public AuthenticationTokenDetails parseToken(String token) {
        return tokenParser.parseToken(token);
    }

    private String generateTokenIdentifier() {
        return UUID.randomUUID().toString();
    }
}
