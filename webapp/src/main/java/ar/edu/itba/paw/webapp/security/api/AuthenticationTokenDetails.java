package ar.edu.itba.paw.webapp.security.api;


import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.api.model.enums.TokenType;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Application model that holds details about an authentication token.
 *
 * @author cassiomolin
 */
public final class AuthenticationTokenDetails {


    private TokenType tokenType;
    private final String id;
    private final String username;
    private final Set<Authority> authorities;
    private final ZonedDateTime issuedDate;
    private final ZonedDateTime expirationDate;

    private AuthenticationTokenDetails(String id, String username, Set<Authority> authorities, ZonedDateTime issuedDate, ZonedDateTime expirationDate, TokenType tokenType) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
        this.issuedDate = issuedDate;
        this.expirationDate = expirationDate;
        this.tokenType = tokenType;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public ZonedDateTime getIssuedDate() {
        return issuedDate;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Builder for the {@link AuthenticationTokenDetails}.
     */
    public static class Builder {

        private String id;
        private String username;
        private Set<Authority> authorities;
        private ZonedDateTime issuedDate;
        private ZonedDateTime expirationDate;
        private TokenType tokenType;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withAuthorities(Set<Authority> authorities) {
            this.authorities = Collections.unmodifiableSet(authorities == null ? new HashSet<>() : authorities);
            return this;
        }

        public Builder withIssuedDate(ZonedDateTime issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder withExpirationDate(ZonedDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder withTokenType(TokenType tokenType){
            this.tokenType = tokenType;
            return this;
        }

        public AuthenticationTokenDetails build() {
            return new AuthenticationTokenDetails(id, username, authorities, issuedDate, expirationDate, tokenType);
        }
    }
}