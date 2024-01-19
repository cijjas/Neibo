package ar.edu.itba.paw.webapp.security.api.jwt;

import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.resource.AuthenticationResource;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication provider for JWT token-based authentication.
 *
 * @author cassiomolin
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOGGER.info("Authenticating Token through the JWT Authentication Provider");

        String authenticationToken = (String) authentication.getCredentials();
        AuthenticationTokenDetails authenticationTokenDetails = authenticationTokenService.parseToken(authenticationToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationTokenDetails.getUsername());

        LOGGER.info("UserForm Authorities : {}", userDetails.getAuthorities());

        // I create an almost identical JWT Authentication Token but this time it has true in the authenticated field
        return new JwtAuthenticationToken(userDetails, authenticationTokenDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}