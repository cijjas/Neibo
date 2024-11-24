package ar.edu.itba.paw.webapp.security.service.impl;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.model.enums.TokenType;
import ar.edu.itba.paw.webapp.security.exception.InvalidAuthenticationTokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Component which provides operations for parsing JWT tokens.
 *
 * @author cassiomolin
 */
@Component
public class JwtTokenParser {

    @Autowired
    private JwtSettings settings;

    /**
     * Parse a JWT token.
     *
     * @param token
     * @return
     */
    public AuthenticationTokenDetails parseToken(String token) {

        try {

            Claims claims = Jwts.parser()
                    .setSigningKey(settings.getSecret())
                    .requireAudience(settings.getAudience())
                    .setAllowedClockSkewSeconds(settings.getClockSkew())
                    .parseClaimsJws(token)
                    .getBody();

            return new AuthenticationTokenDetails.Builder()
                    .withId(extractTokenIdFromClaims(claims))
                    .withUsername(extractUsernameFromClaims(claims))
                    .withAuthorities(extractAuthoritiesFromClaims(claims))
                    .withIssuedDate(extractIssuedDateFromClaims(claims))
                    .withExpirationDate(extractExpirationDateFromClaims(claims))
                    .withTokenType(extractTokenTypeFromClaims(claims))
                    .build();

        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e) {
            throw new InvalidAuthenticationTokenException("Invalid token", e);
        } catch (InvalidClaimException e) {
            throw new InvalidAuthenticationTokenException("Invalid value for claim \"" + e.getClaimName() + "\"", e);
        } catch (Exception e) {
            throw new InvalidAuthenticationTokenException("Invalid token", e);
        }
    }

    /**
     * Extract the token identifier from the token claims.
     *
     * @param claims
     * @return Identifier of the JWT token
     */
    private String extractTokenIdFromClaims(@NotNull Claims claims) {
        return (String) claims.get(Claims.ID);
    }

    /**
     * Extract the username from the token claims.
     *
     * @param claims
     * @return Username from the JWT token
     */
    private String extractUsernameFromClaims(@NotNull Claims claims) {
        return claims.getSubject();
    }

    private TokenType extractTokenTypeFromClaims(@NotNull Claims claims) {
        String tokenTypeClaim = claims.get(settings.getTokenTypeClaimName(), String.class);
        try {
            return TokenType.valueOf(tokenTypeClaim.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidAuthenticationTokenException("Invalid token type claim: " + tokenTypeClaim, e);
        }
    }

    /**
     * Extract the user authorities from the token claims.
     *
     * @param claims
     * @return UserForm authorities from the JWT token
     */
    private Set<Authority> extractAuthoritiesFromClaims(@NotNull Claims claims) {
        List<String> rolesAsString = (List<String>) claims.getOrDefault(settings.getAuthoritiesClaimName(), new ArrayList<>());
        return rolesAsString.stream().map(Authority::valueOf).collect(Collectors.toSet());
    }

    /**
     * Extract the issued date from the token claims.
     *
     * @param claims
     * @return Issued date of the JWT token
     */
    private ZonedDateTime extractIssuedDateFromClaims(@NotNull Claims claims) {
        return ZonedDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault());
    }

    /**
     * Extract the expiration date from the token claims.
     *
     * @param claims
     * @return Expiration date of the JWT token
     */
    private ZonedDateTime extractExpirationDateFromClaims(@NotNull Claims claims) {
        return ZonedDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
    }
}
