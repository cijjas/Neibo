package ar.edu.itba.paw.webapp.security.service;


import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;

import java.util.Set;

/**
 * Service which provides operations for authentication tokens.
 *
 * @author cassiomolin
 */
public interface AuthenticationTokenService {

    /**
     * Issue an authentication token for a user with the given authorities.
     *
     * @param username
     * @param authorities
     * @return
     */
    String issueAccessToken(String username, Set<Authority> authorities);

    /**
     * Parse an authentication token.
     *
     * @param token
     * @return
     */
    AuthenticationTokenDetails parseToken(String token);

    String issueRefreshToken(String username, Set<Authority> authorities);
}
