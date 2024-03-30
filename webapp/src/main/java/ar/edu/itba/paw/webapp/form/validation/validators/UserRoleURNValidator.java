package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UserRoleURNValidator implements ConstraintValidator<UserRoleURNConstraint, String> {
    @Override
    public void initialize(UserRoleURNConstraint userRoleURNConstraint) {}

    @Override
    public boolean isValid(String userRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        if(userRoleURN==null)
            return true;
        return URNValidator.validateURN(userRoleURN, "userRole");
    }
}