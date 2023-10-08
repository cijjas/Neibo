package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final SessionUtils sessionUtils;

    @Autowired
    public GlobalControllerAdvice(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }

    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        return sessionUtils.getLoggedUser();
    }
}