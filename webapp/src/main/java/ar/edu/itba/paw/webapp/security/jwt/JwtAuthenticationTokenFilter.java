package ar.edu.itba.paw.webapp.security.jwt;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.AuthenticationTokenDetails;
import ar.edu.itba.paw.webapp.security.UserAuth;
import ar.edu.itba.paw.webapp.security.enums.TokenType;
import ar.edu.itba.paw.webapp.security.exception.ExpiredTokenException;
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

        LOGGER.debug("JWT Authentication Token Filter activated");

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null) {
            if (authorizationHeader.startsWith("Bearer ")) {
                if (!handleJwtAuthentication(authorizationHeader, request, response))
                    return;
            } else if (authorizationHeader.startsWith("Basic ")) {
                if (!handleBasicAuthentication(authorizationHeader, request, response))
                    return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean handleBasicAuthentication(String authorizationHeader, HttpServletRequest request,
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
            response.addHeader("X-Access-Token", jwtToken);
            response.addHeader("X-Refresh-Token", refreshToken);

            // Construct a full URL for the User URL, Workers' Neighborhood and User's Neighborhood URL and add them to the response headers
            UserAuth userAuth = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userAuth.getNeighborhoodId() != 0) {
                String workersNeighborhoodURL = String.format("%s://%s:%d%s/neighborhoods/%d",
                        request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), 0);
                response.addHeader("X-Workers-Neighborhood-URL", Link.fromUri(workersNeighborhoodURL).rel("workers-neighborhood-url").build().toString());
            }
            String neighborhoodURL = String.format("%s://%s:%d%s/neighborhoods/%d",
                    request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(),
                    userAuth.getNeighborhoodId());
            response.addHeader("X-Neighborhood-URL", Link.fromUri(neighborhoodURL).rel("neighborhood-url").build().toString());
            String userURL = String.format("%s://%s:%d%s/users/%d",
                    request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), userAuth.getUserId());
            response.addHeader("X-User-URL", Link.fromUri(userURL).rel("user-url").build().toString());
        } catch (AuthenticationException e) {
            LOGGER.debug("Invalid Basic Authentication provided");
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return false;
        }
        LOGGER.debug("Valid Basic Authentication provided");
        return true;
    }

    private boolean handleJwtAuthentication(String authorizationHeader, HttpServletRequest request,
                                            HttpServletResponse response) throws IOException, ServletException {
        try {
            String authenticationToken = authorizationHeader.substring(7);

            AuthenticationTokenDetails tokenDetails = authenticationTokenService.parseToken(authenticationToken);

            if (tokenDetails.getTokenType() == TokenType.REFRESH) {
                if (ZonedDateTime.now().isAfter(tokenDetails.getExpirationDate()))
                    throw new ExpiredTokenException("Refresh token has expired");
                String newAccessToken = authenticationTokenService.issueAccessToken(tokenDetails.getUsername(), tokenDetails.getAuthorities());
                response.addHeader("X-Access-Token", newAccessToken);
            }

            if (ZonedDateTime.now().isAfter(tokenDetails.getExpirationDate()))
                throw new ExpiredTokenException("Access token has expired");

            Authentication authenticationRequest = new JwtAuthenticationToken(authenticationToken);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);
        } catch (ExpiredTokenException e) {
            LOGGER.debug("Expired token provided", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return false;
        } catch (AuthenticationException e) {
            LOGGER.debug("Invalid access token provided", e);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return false;
        }
        LOGGER.debug("Valid access token provided");
        return true;
    }

    /*
     * If persistence is implemented as well as the Refreshing strategy, this method should verify this info with the DB
     * */
    private boolean handleRefreshToken(String refreshHeader, HttpServletRequest request,
                                       HttpServletResponse response) throws IOException, ServletException {
        try {
            String refreshToken = refreshHeader.substring(7);
            AuthenticationTokenDetails tokenDetails = authenticationTokenService.parseToken(refreshToken);

            if (tokenDetails.getTokenType() != TokenType.REFRESH)
                throw new IllegalArgumentException("Invalid Token");

            if (ZonedDateTime.now().isAfter(tokenDetails.getExpirationDate()))
                throw new IllegalArgumentException("Refresh token has expired");

            String newAccessToken = authenticationTokenService.issueAccessToken(tokenDetails.getUsername(), tokenDetails.getAuthorities());
            response.addHeader("X-Access-Token", newAccessToken);

        } catch (AuthenticationException e) {
            LOGGER.debug("Error processing refresh token", e);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return false;
        } catch (IllegalArgumentException e) {
            LOGGER.debug("Invalid refresh token provided", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid refresh token");
            return false;
        }
        LOGGER.debug("Valid refresh token provided");
        return true;
    }
}