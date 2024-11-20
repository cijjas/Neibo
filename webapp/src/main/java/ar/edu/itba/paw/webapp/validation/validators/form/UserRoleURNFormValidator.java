package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserRoleURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UserRoleURNFormValidator implements ConstraintValidator<UserRoleURNFormConstraint, String> {
    @Override
    public void initialize(UserRoleURNFormConstraint userRoleURNConstraint) {
    }

    @Override
    public boolean isValid(String userRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userRoleURN, "userRole");
    }
}