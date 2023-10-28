package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.models.MainEntities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionUtils {

    private final SessionUserCache sessionUserCache;
    private final UserService us;

    private final NeighborhoodService ns;

    @Autowired
    public SessionUtils(SessionUserCache sessionUserCache, UserService userService, NeighborhoodService ns) {
        this.us = userService;
        this.sessionUserCache = sessionUserCache;
        this.ns = ns;
    }

    public String getLoggedUserNeighborhoodName() {
        User user = getLoggedUser();
        if (user == null) {
            return null;
        }
        Optional<Neighborhood> neighborhood = ns.findNeighborhoodById(getLoggedUser().getNeighborhood().getNeighborhoodId());
        Neighborhood n = neighborhood.orElseThrow(() -> new NotFoundException("Neighborhood Not Found"));
        return n.getName();
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
        Optional<User> neighborOptional = us.findUserByMail(email);

        User user = neighborOptional.orElseThrow(() -> new NotFoundException("Neighbor Not Found"));
        sessionUserCache.setCachedUser(user);
        return user;
    }

    public void clearLoggedUser() {
        sessionUserCache.clearCache();
    }
}
