package ar.edu.itba.paw.webapp.security.api.jwt;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import ar.edu.itba.paw.webapp.security.service.AuthenticationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Link;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter for JWT token-based authentication.
 * <p>
 * Authentication tokens must be sent in the <code>Authorization</code> header and prefixed with <code>Bearer</code>:
 * <p>
 * <pre>
 *     Authorization: Bearer xxx.yyy.zzz
 * </pre>
 *
 * @author cassiomolin
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationTokenService authenticationTokenService;

    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager,
                                        AuthenticationEntryPoint authenticationEntryPoint,
                                        AuthenticationTokenService authenticationTokenService) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationTokenService = authenticationTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        LOGGER.info("JWT Authentication Token Filter activated");

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null) {
            if (authorizationHeader.startsWith("Bearer ")) {
                // JWT token provided, proceed with JWT authentication
                handleJwtAuthentication(authorizationHeader, request, response);
            } else if (authorizationHeader.startsWith("Basic ")) {
                // Basic Auth credentials provided, proceed with Basic Auth authentication
                handleBasicAuthentication(authorizationHeader, request, response);
            }
        }

        // Refresh
        // how should the refresh be handled? filters!

        LOGGER.info("Filter Chaining");
        filterChain.doFilter(request, response);
    }

    private void handleBasicAuthentication(String authorizationHeader, HttpServletRequest request,
                                           HttpServletResponse response) throws IOException, ServletException {
        try {
            // Decode and extract Basic Auth credentials
            String credentialsBase64 = authorizationHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(credentialsBase64));
            String[] usernamePassword = credentials.split(":");
            // Perform authentication using the provided username and password
            Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(usernamePassword[0], usernamePassword[1]);
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
            // Get neighborhoodId and userId to build the URN

            // could somehow utilize current url to build a more useful version of this
            UserAuth userAuth = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String urn = String.format("/neighborhoods/%d/users/%d", userAuth.getNeighborhoodId(), userAuth.getUserId());
            Link userURN = Link.fromUri(urn).rel("urn").build();
            // Add required headers
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            response.addHeader("X-User-URN", userURN.toString());
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    private void handleJwtAuthentication(String authorizationHeader, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException, ServletException {
        try {
            String authenticationToken = authorizationHeader.substring(7);
            Authentication authenticationRequest = new JwtAuthenticationToken(authenticationToken);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);

            LOGGER.info("Security Context Filled");
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}