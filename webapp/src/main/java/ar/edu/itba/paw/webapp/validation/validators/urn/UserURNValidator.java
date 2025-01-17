package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class UserURNValidator implements ConstraintValidator<UserURNConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UserURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext context) {
        if (userURN == null)
            return true;
        if (!URNValidator.validateURN(userURN, Endpoint.USERS))
            return false;
        return userService.findUser(extractFirstId(userURN)).isPresent();
    }
}
