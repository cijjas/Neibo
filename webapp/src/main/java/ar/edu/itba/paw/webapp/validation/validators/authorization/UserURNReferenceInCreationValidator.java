package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInCreationConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNReferenceInCreationValidator implements ConstraintValidator<UserURNReferenceInCreationConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserURNReferenceInCreationConstraint userURNReferenceConstraintCreate) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userURN == null)
            return true;
        if (!formAccessControlHelper.canReferenceUserInCreation(userURN)){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FORBIDDEN")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}