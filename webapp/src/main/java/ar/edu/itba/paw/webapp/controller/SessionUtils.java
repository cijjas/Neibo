package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionUtils {

    private final SessionUserCache sessionUserCache;
    private final UserService userService;

    @Autowired
    public SessionUtils(SessionUserCache sessionUserCache, UserService userService) {
        this.userService = userService;
        this.sessionUserCache = sessionUserCache;
    }

    public User getLoggedUser() {
        User cachedUser = sessionUserCache.getCachedUser();
        if (cachedUser != null) {
            return cachedUser;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = authentication.getName();
        Optional<User> neighborOptional = userService.findUserByMail(email);

        User user = neighborOptional.orElseThrow(() -> new NotFoundException("Neighbor Not Found"));
        sessionUserCache.setCachedUser(user);
        return user;
    }

    public void clearLoggedUser() {
        sessionUserCache.clearCache();
    }
}
