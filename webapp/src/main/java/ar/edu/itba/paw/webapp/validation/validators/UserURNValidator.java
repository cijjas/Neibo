package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNValidator implements ConstraintValidator<UserURNConstraint, String> {

    @Override
    public void initialize(UserURNConstraint userURNConstraint) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userURN, "users");
    }
}