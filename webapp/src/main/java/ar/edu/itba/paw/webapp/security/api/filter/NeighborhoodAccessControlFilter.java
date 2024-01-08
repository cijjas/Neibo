package ar.edu.itba.paw.webapp.security.api.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NeighborhoodAccessControlFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        if (authentication != null && authentication.isAuthenticated()) {
            String requestedPath = getRequestPath(request);

            if (isRestrictedPath(requestedPath) && !isAuthorizedForNeighborhood(authentication, requestedPath)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private boolean isRestrictedPath(String requestedPath) {
        return requestedPath.matches("/neighborhoods/\\d+/.+");  // Adjust the regex as needed
    }

    private boolean isAuthorizedForNeighborhood(Authentication authentication, String requestedPath) {
        // Implement your logic to check if the user has access to the requested neighborhood
        // You can extract information from the authentication object, such as authorities or user details
        // You can also analyze the requested path to determine the neighborhood
        // Return true if access is allowed, false otherwise
        return true;  // Replace with your logic
    }
}
