package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class AccessControlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlHelper.class);

    @Autowired
    private UserService us;

    // All requests that correspond to a certain Neighborhood have to be done from a User from that same Neighborhood
    public boolean isNeighborhoodMember(HttpServletRequest request) {
        LOGGER.info("Neighborhood Belonging Bind");
        System.out.println("Neighborhood Belonging Bind");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return false;
        }

        Long userNeighborhoodId = userAuth.getNeighborhoodId();

        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");

        if (uriParts.length >= 3 && uriParts[1].equals("neighborhoods")) {
            try {
                Long neighborhoodIdFromURL = Long.parseLong(uriParts[2]);
                return userNeighborhoodId.equals(neighborhoodIdFromURL);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }


    // Unverified and Rejected Users can´t filter with a workerId the /neighborhoods endpoint
    public boolean hasAccessNeighborhoodQP(Long workerId) {
        LOGGER.info("Verifying Query Params Accessibility");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (workerId != null) {
                return authentication.getAuthorities().stream()
                        .noneMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"));
            } else {
                return true;
            }
        }

        return workerId == null;
    }

    // Rejected and Unverified Users cant access users other than themselves
    public boolean hasAccessToAllUsers(long neighborhoodId, long userId) {
        LOGGER.info("Verifying Detail User Accessibility");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        if (userAuth.getNeighborhoodId() != neighborhoodId)
            return false;

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_UNVERIFIED_NEIGHBOR") || authority.getAuthority().equals("ROLE_REJECTED"))) {
            return ((UserAuth) authentication.getPrincipal()).getUserId() == userId;
        }

        return true;
    }

    // Only an Administrator can update other people's profile
    public Boolean canUpdateAnyone(long userId) {
        LOGGER.info("Verifying Update User Accessibility");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();

        // Check if the user has the role of an administrator
        if (userAuth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRATOR")))
            return true;

        // Check if the user is trying to update themselves
        return userAuth.getUserId() == userId;
    }
}
