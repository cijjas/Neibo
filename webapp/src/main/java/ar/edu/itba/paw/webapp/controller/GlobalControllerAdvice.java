package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    public long getLoggedUserId() {
        return (((UserAuth)SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUserId();
    }

    public long getLoggedNeighborhoodId() {
        return (((UserAuth)SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getNeighborhoodId();
    }
}