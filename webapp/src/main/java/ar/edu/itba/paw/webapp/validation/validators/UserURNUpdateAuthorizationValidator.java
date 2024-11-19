package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraintCreate;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraintUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNUpdateAuthorizationValidator implements ConstraintValidator<UserURNReferenceConstraintUpdate, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNReferenceConstraintUpdate userURNReferenceConstraintUpdate) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userURN, "users") && accessControlHelper.canReferenceUserInUpdate(userURN);
    }
}