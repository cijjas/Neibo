package ar.edu.itba.paw.webapp.security.api.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpMethodFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestedPath = getRequestPath(request);
        String httpMethod = request.getMethod();

        if (isRestrictedMethodForPath(httpMethod, requestedPath)) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private boolean isRestrictedMethodForPath(String httpMethod, String requestedPath) {
        // Implement your logic to check if the HTTP method is allowed for the requested path
        // Return true if access is denied, false otherwise
        return false;  // Replace with your logic
    }
}
