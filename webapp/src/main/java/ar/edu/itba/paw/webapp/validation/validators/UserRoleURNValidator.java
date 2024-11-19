package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.UserRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UserRoleURNValidator implements ConstraintValidator<UserRoleURNConstraint, String> {
    @Override
    public void initialize(UserRoleURNConstraint userRoleURNConstraint) {
    }

    @Override
    public boolean isValid(String userRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userRoleURN, "userRole");
    }
}