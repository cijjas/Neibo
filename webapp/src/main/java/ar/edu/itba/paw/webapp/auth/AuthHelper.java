package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.Authority;
import ar.edu.itba.paw.webapp.security.UserAuth;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthHelper {
    Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    boolean isUnverifiedOrRejected(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_UNVERIFIED_NEIGHBOR.name()) ||
                        authority.getAuthority().equals(Authority.ROLE_REJECTED.name()));
    }

    boolean isAnonymous(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    boolean isAdministrator(Authentication authentication) {
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_ADMINISTRATOR.name()));
    }

    boolean isSuperAdministrator(Authentication authentication) {
        return getRequestingUser(authentication).getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Authority.ROLE_SUPER_ADMINISTRATOR.name()));
    }

    long getRequestingUserId(Authentication authentication) {
        return getRequestingUser(authentication).getUserId();
    }

    long getRequestingUserNeighborhoodId(Authentication authentication) {
        return getRequestingUser(authentication).getNeighborhoodId();
    }

    UserAuth getRequestingUser(Authentication authentication) {
        return (UserAuth) authentication.getPrincipal();
    }
}
