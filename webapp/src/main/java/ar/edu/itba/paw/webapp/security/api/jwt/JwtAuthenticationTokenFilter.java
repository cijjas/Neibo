package ar.edu.itba.paw.webapp.security.api.jwt;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import ar.edu.itba.paw.webapp.security.api.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.api.model.enums.TokenType;
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
import java.time.ZonedDateTime;
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
            if (authorizationHeader.startsWith("Bearer "))
                handleJwtAuthentication(authorizationHeader, request, response);
            else if (authorizationHeader.startsWith("Basic "))
                handleBasicAuthentication(authorizationHeader, request, response);
        }

        System.out.println("Arrived here");
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null)
            handleRefreshToken(refreshHeader, request, response);

        LOGGER.info("Filter Chaining");
        filterChain.doFilter(request, response);
    }

    private void handleBasicAuthentication(String authorizationHeader, HttpServletRequest request,
                                           HttpServletResponse response) throws IOException, ServletException {
        try {
            // Decode the credentials from the Authorization header
            String credentialsBase64 = authorizationHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(credentialsBase64));
            String[] usernamePassword = credentials.split(":");

            // Create authentication request using username and password
            Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(usernamePassword[0], usernamePassword[1]);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

            // Set the authenticated user context
            SecurityContextHolder.getContext().setAuthentication(authenticationResult);

            // Get the username and authorities from the authenticated context
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Set<Authority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(grantedAuthority -> Authority.valueOf(grantedAuthority.toString()))
                    .collect(Collectors.toSet());

            // Issue a short-lived JWT token
            String jwtToken = authenticationTokenService.issueAccessToken(username, authorities);

            // Issue a long-lived refresh token
            String refreshToken = authenticationTokenService.issueRefreshToken(username, authorities);

            // Add both tokens to the response headers
            response.addHeader("X-JSON-Web-Token", "Bearer " + jwtToken);
            response.addHeader("X-Refresh-Token", "Bearer " + refreshToken);

            // Construct a full URL for the User URN and add it to the response headers
            UserAuth userAuth = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String fullURL = String.format("%s://%s:%d%s/neighborhoods/%d/users/%d",
                    request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(),
                    userAuth.getNeighborhoodId(), userAuth.getUserId());
            response.addHeader("X-User-URN", Link.fromUri(fullURL).rel("urn").build().toString());
        } catch (AuthenticationException e) {
            // Clear security context and invoke authentication entry point on failure
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
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    /*
    * If persistence is implemented as well as the Refreshing strategy, this method should verify this info with the DB
    * */
    private void handleRefreshToken(String refreshHeader, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException, ServletException {
        try {
            String refreshToken = refreshHeader.substring(7);
            System.out.println(refreshToken);
            AuthenticationTokenDetails tokenDetails = authenticationTokenService.parseToken(refreshToken);

            if (tokenDetails.getTokenType() != TokenType.REFRESH)
                throw new IllegalArgumentException("Invalid Token");

            if (ZonedDateTime.now().isAfter(tokenDetails.getExpirationDate()))
                throw new IllegalArgumentException("Refresh token has expired");

            String newAccessToken = authenticationTokenService.issueAccessToken(tokenDetails.getUsername(), tokenDetails.getAuthorities());
            response.addHeader("X-JSON-Web-Token", "Bearer " + newAccessToken);

            LOGGER.info("Refresh token successfully processed for user: {}", tokenDetails.getUsername());
        } catch (AuthenticationException e) {
            LOGGER.error("Error processing refresh token", e);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid refresh token provided", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid refresh token");
        }
    }
}