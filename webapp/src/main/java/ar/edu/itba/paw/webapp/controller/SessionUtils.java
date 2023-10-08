package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@Component
class SessionUtils {

    private final UserService userService;

    @Autowired
    public SessionUtils(UserService userService) {
        this.userService = userService;
    }



    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
            return null;
        String email = authentication.getName();
        Optional<User> neighborOptional = userService.findUserByMail(email);
        return neighborOptional.orElseThrow(() -> new NotFoundException("Neighbor Not Found"));
    }
}