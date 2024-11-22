package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserRoleURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class UserRoleURNReferenceValidator implements ConstraintValidator<UserRoleURNReferenceConstraint, String> {

    @Override
    public void initialize(UserRoleURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String userRoleURN, ConstraintValidatorContext context) {
        if (userRoleURN == null || userRoleURN.trim().isEmpty())
            return true;
        Long userRoleId = extractOptionalFirstId(userRoleURN);
        if (userRoleId == null)
            return true;
        try {
            UserRole.fromId(userRoleId);
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
