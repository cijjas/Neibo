package ar.edu.itba.paw.webapp.security.service;


import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.AuthenticationTokenDetails;

import java.util.Set;

public interface AuthenticationTokenService {

    String issueAccessToken(String username, Set<Authority> authorities);

    AuthenticationTokenDetails parseToken(String token);

    String issueRefreshToken(String username, Set<Authority> authorities);
}
