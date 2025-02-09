package ar.edu.itba.paw.webapp.security.jwt;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
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
            String credentialsBase64 = authorizationHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(credentialsBase64));
            String[] usernamePassword = credentials.split(":");

            Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(usernamePassword[0], usernamePassword[1]);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

            SecurityContextHolder.getContext().setAuthentication(authenticationResult);

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Set<Authority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(grantedAuthority -> Authority.valueOf(grantedAuthority.toString()))
                    .collect(Collectors.toSet());

            String jwtToken = authenticationTokenService.issueAccessToken(username, authorities);

            String refreshToken = authenticationTokenService.issueRefreshToken(username, authorities);

            response.addHeader("X-Access-Token", jwtToken);
            response.addHeader("X-Refresh-Token", refreshToken);

            UserAuth userAuth = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (userAuth.getNeighborhoodId() != BaseNeighborhood.WORKERS.getId()) {
                String workersNeighborhoodURL = String.format("%s://%s:%d%s/%s/%s/%d",
                        request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), Endpoint.API, Endpoint.NEIGHBORHOODS, BaseNeighborhood.WORKERS.getId());
                response.addHeader("X-Workers-Neighborhood-URL", Link.fromUri(workersNeighborhoodURL).rel("workers-neighborhood-url").build().toString());
            }
            String neighborhoodURL = String.format("%s://%s:%d%s/%s/%s/%d",
                    request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), Endpoint.API, Endpoint.NEIGHBORHOODS, userAuth.getNeighborhoodId());
            response.addHeader("X-Neighborhood-URL", Link.fromUri(neighborhoodURL).rel("neighborhood-url").build().toString());
            String userURL = String.format("%s://%s:%d%s/%s/%s/%d",
                    request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), Endpoint.API, Endpoint.USERS, userAuth.getUserId());
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
}