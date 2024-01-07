package ar.edu.itba.paw.webapp.security.api.resource;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.form.LoginForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.model.AuthenticationToken;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    /**
     * Validate user credentials and issue a token for the user.
     *
     * @param credentials
     * @return
     */
    @POST
    public Response authenticate(LoginForm credentials) {
        System.out.println(credentials);
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(credentials.getMail(), credentials.getPassword());
        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResult);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<Authority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(grantedAuthority -> Authority.valueOf(grantedAuthority.toString()))
                .collect(Collectors.toSet());

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
    @Path("refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh() {

        AuthenticationTokenDetails tokenDetails = (AuthenticationTokenDetails)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        String token = authenticationTokenService.refreshToken(tokenDetails);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(token);

        return Response.ok(authenticationToken).build();
    }

    @GET
    public String testing(){
        return "this worked";
    }
}
