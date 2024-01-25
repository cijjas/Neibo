package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AccessControlHelper {

    @Autowired
    private UserService us;

    public boolean isNeighborhoodMember(HttpServletRequest request) {
        System.out.println("Neighborhood Heavenly Bind");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return false;
        }

        Optional<User> userOptional = us.findUser(userAuth.getUsername());

        if (!userOptional.isPresent())
            return false;

        User user = userOptional.get();

        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");
        if (uriParts.length >= 3 && uriParts[1].equals("neighborhoods")) {
            try {
                Long neighborhoodIdFromURL = Long.parseLong(uriParts[2]);
                return user.getNeighborhood().getNeighborhoodId().equals(neighborhoodIdFromURL);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }
}
