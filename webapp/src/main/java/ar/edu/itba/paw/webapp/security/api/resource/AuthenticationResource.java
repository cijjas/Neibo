package ar.edu.itba.paw.webapp.security.api.resource;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.form.LoginForm;
import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.model.AuthenticationToken;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JAX-RS resource class for authentication. Username and password are exchanged for an authentication token.
 *
 * @author cassiomolin
 */
@Component
@Path("auth")
public class AuthenticationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    /*
    * Security Context is constantly used even though it seems like something more commonly used in stateful implementations
    * this is because it allows for SSOT across the multiple layers that are involved in the authentication, the JWT Token Filter
    * which is the first thing that processes each HTTP request is the one in charge of filling it for the first time, due to
    * that all the methods like the ones in this class can safely use the Security Context knowing this is not conflicting
    * with other clients' requests, as the context is unique per request
    */

    /**
     * Validate user credentials and issue a token for the user.
     *
     * @param credentials
     * @return
     */
    @POST
    public Response authenticate(LoginForm credentials) {
        LOGGER.info("Client accessing '/auth'");

        // Saving the request
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(credentials.getMail(), credentials.getPassword());
        // Call the Authentication Manager that calls the Provider which then calls LoadByUsername()
        // This method automatically throws an exception if the credentials are not correct
        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
        // Set the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationResult);

        // Get username from the Security Context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Get authorities from the Security Context
        Set<Authority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(grantedAuthority -> Authority.valueOf(grantedAuthority.toString()))
                .collect(Collectors.toSet());

        // Issue Token
        String token = authenticationTokenService.issueToken(username, authorities);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(token);

        return Response.ok(authenticationToken).build();
    }

    /**
     * Refresh the authentication token for the current user.
     *
     * @return
     */
    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh() {
        LOGGER.info("Client accessing '/auth/refresh'");

        AuthenticationTokenDetails tokenDetails = (AuthenticationTokenDetails)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        String token = authenticationTokenService.refreshToken(tokenDetails);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(token);

        return Response.ok(authenticationToken).build();
    }
}
