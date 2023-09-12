package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("errors/errorPage");
        String errorMsg = "";
        String errorCode="";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
            case 400: {
                errorCode="400";
                errorMsg = "Bad Request";
                break;
            }
            case 401: {
                errorCode="401";
                errorMsg = "Unauthorized";
                break;
            }
            case 404: {
                errorCode="404";
                errorMsg = "Page not found";
                break;
            }
            case 500: {
                errorCode="500";
                errorMsg ="Internal Server Error";
                break;
            }
        }
        errorPage.addObject("errorCode", errorCode);
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }
}
