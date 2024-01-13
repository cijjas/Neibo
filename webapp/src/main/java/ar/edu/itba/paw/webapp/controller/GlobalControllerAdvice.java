package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    private final UserService us;

    @Autowired
    public GlobalControllerAdvice(final UserService us) {
        this.us = us;
    }

    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        String mail = (((UserAuth)SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUsername();
        if(mail == null) {
            throw new NotFoundException("User Not Found");
        }

        return us.findUserByMail(mail).orElseThrow(() -> new NotFoundException("User Not Found"));
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken)
//            return null;
//
//        String email = authentication.getName();
//
//        return us.findUserByMail(email).orElseThrow(()-> new NotFoundException("User Not Found"));
    }
}