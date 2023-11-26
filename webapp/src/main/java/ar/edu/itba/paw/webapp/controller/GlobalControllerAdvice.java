package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.*;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    private final SessionUtils sessionUtils;

    @Autowired
    public GlobalControllerAdvice(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }

    @ModelAttribute("loggedUser")
    @Cacheable("loggedUser")
    public User getLoggedUser() {
        return sessionUtils.getLoggedUser();
    }

    // -------------------------------------------------- EXCEPTIONS ---------------------------------------------------

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView notFound(RuntimeException ex) {
        LOGGER.info("Not Found Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "404");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView duplicated(RuntimeException ex) {
        LOGGER.info("Duplicate Key Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "409"); // 409 = Conflict
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(InsertionException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView insertion(RuntimeException ex) {
        LOGGER.info("Insertion Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MailingException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView mailing(RuntimeException ex) {
        LOGGER.info("Mailing Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UnexpectedException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView unexpected(RuntimeException ex) {
        LOGGER.info("Unexpected Exception was Thrown", ex);
        ModelAndView mav = new ModelAndView("errors/errorPage");
        mav.addObject("errorCode", "500");
        mav.addObject("errorMsg", ex.getMessage());
        return mav;
    }
}