package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.PhoneNumberConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(PhoneNumberConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userURI, ConstraintValidatorContext context) {
        if (userURI == null)
            return true;
        if (!URIValidator.validateOptionalURI(userURI, Endpoint.USERS))
            return false;
        return userService.findUser(extractFirstId(userURI)).orElseThrow(NotFoundException::new).getPhoneNumber() != null;
    }
}
