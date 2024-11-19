package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraintCreate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNCreateAuthorizationValidator implements ConstraintValidator<UserURNReferenceConstraintCreate, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNReferenceConstraintCreate userURNReferenceConstraintCreate) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userURN, "users") && accessControlHelper.canReferenceUserInCreation(userURN);
    }
}