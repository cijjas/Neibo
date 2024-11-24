package ar.edu.itba.paw.webapp.security.api.jwt;

import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.webapp.security.exception.InvalidAuthenticationTokenException;
import ar.edu.itba.paw.webapp.security.exception.InvalidTokenTypeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

    @Autowired
    private XmlMapper xmlMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOGGER.info("Handling Authentication Exception");

        HttpStatus status = authException instanceof InvalidAuthenticationTokenException ||
                authException instanceof InvalidTokenTypeException
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.FORBIDDEN;

        ApiErrorDetails errorDetails = createErrorDetails(authException.getMessage(), authException.getCause(), status, request);
        prepareAndWriteResponse(request, response, errorDetails, status);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.info("Handling Access Denied Exception");

        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiErrorDetails errorDetails = createErrorDetails(status.getReasonPhrase(), accessDeniedException, status, request);
        prepareAndWriteResponse(request, response, errorDetails, status);
    }

    private ApiErrorDetails createErrorDetails(String title, Throwable exception, HttpStatus status, HttpServletRequest request) {
        ApiErrorDetails errorDetails = new ApiErrorDetails();
        errorDetails.setTitle(title);
        // Only set the message if it's not null
        if (exception != null && exception.getMessage() != null) {
            errorDetails.setMessage(exception.getMessage());
        }
        errorDetails.setStatus(status.value());
        errorDetails.setPath(request.getRequestURI());
        return errorDetails;
    }

    private void prepareAndWriteResponse(HttpServletRequest request, HttpServletResponse response, ApiErrorDetails errorDetails, HttpStatus status) throws IOException {
        response.setStatus(status.value());

        String contentType = determineContentType(request);
        response.setContentType(contentType);

        writeResponse(response, errorDetails, contentType);
    }

    private String determineContentType(HttpServletRequest request) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
            return MediaType.APPLICATION_XML_VALUE;
        }
        return MediaType.APPLICATION_JSON_VALUE;
    }

    private void writeResponse(HttpServletResponse response, ApiErrorDetails errorDetails, String contentType) throws IOException {
        String responseBody;
        if (MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
            responseBody = xmlMapper.writeValueAsString(errorDetails);
        } else {
            responseBody = objectMapper.writeValueAsString(errorDetails);
        }
        response.getWriter().write(responseBody);
    }
}
