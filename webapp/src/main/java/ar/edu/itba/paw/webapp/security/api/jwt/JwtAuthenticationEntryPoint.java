package ar.edu.itba.paw.webapp.security.api.jwt;

import ar.edu.itba.paw.models.ApiErrorDetails;
import ar.edu.itba.paw.webapp.security.exception.InvalidAuthenticationTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Entry point for JWT token-based authentication. Simply returns error details related to authentication failures.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        HttpStatus status;
        ApiErrorDetails errorDetails = new ApiErrorDetails();

        if (authException instanceof InvalidAuthenticationTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            errorDetails.setTitle(authException.getMessage());
            errorDetails.setMessage(authException.getCause().getMessage());
        } else {
            status = HttpStatus.FORBIDDEN;
            errorDetails.setTitle(status.getReasonPhrase());
            errorDetails.setMessage(authException.getMessage());
        }

        errorDetails.setStatus(status.value());
        errorDetails.setPath(request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        writeJsonResponse(response, errorDetails);
    }

    // todo improve this temp solution, maybe using a jackson mapper is the only way to go :(((
    private void writeJsonResponse(HttpServletResponse response, ApiErrorDetails errorDetails) throws IOException {
        String jsonResponse = "{"
                + "\"status\":\"" + errorDetails.getStatus() + "\","
                + "\"title\":\"" + errorDetails.getTitle() + "\","
                + "\"message\":\"" + errorDetails.getMessage() + "\","
                + "\"path\":\"" + errorDetails.getPath() + "\""
                + "}";


        response.getWriter().write(jsonResponse);
    }
}
