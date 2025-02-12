package ar.edu.itba.paw.webapp.security.jwt;

import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.webapp.security.exception.InvalidAuthenticationTokenException;
import ar.edu.itba.paw.webapp.security.exception.InvalidTokenTypeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        LOGGER.debug("Handling Authentication Exception");

        HttpStatus status = (authException instanceof InvalidAuthenticationTokenException ||
                authException instanceof InvalidTokenTypeException)
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        response.addHeader(HttpHeaders.WWW_AUTHENTICATE,
                "Basic realm=\"NeiboAPI\", Bearer realm=\"NeiboAPI\"");

        ApiErrorDetails errorDetails = createErrorDetails(authException.getMessage(),
                authException.getCause(),
                status, request);
        writeResponse(response, errorDetails, status);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        LOGGER.debug("Handling Access Denied Exception");

        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorDetails errorDetails = createErrorDetails(status.getReasonPhrase(),
                accessDeniedException,
                status, request);
        writeResponse(response, errorDetails, status);
    }

    private ApiErrorDetails createErrorDetails(String title, Throwable exception,
                                               HttpStatus status, HttpServletRequest request) {
        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setTitle(title);
        if (exception != null && exception.getMessage() != null) {
            errorDetails.setMessage(exception.getMessage());
        }
        errorDetails.setStatus(status.value());
        errorDetails.setPath(request.getRequestURI());
        return errorDetails;
    }

    private void writeResponse(HttpServletResponse response, ApiErrorDetails errorDetails,
                               HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        response.getWriter().flush();
    }
}