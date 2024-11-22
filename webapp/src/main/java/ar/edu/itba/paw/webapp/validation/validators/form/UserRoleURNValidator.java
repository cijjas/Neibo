package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;


public class UserRoleURNValidator implements ConstraintValidator<UserRoleURNConstraint, String> {

    @Override
    public void initialize(UserRoleURNConstraint userRoleURNConstraint) {}

    @Override
    public boolean isValid(String userRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userRoleURN == null)
            return true;
        if (!URNValidator.validateURN(userRoleURN, "userRole"))
            return false;
        try {
            UserRole.fromId(extractFirstId(userRoleURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}