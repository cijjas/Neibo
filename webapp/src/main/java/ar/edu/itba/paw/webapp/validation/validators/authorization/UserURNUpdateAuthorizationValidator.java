package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceConstraintUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNUpdateAuthorizationValidator implements ConstraintValidator<UserURNReferenceConstraintUpdate, String> {

    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNReferenceConstraintUpdate userURNReferenceConstraintUpdate) {}

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userURN == null)
            return true;
        return accessControlHelper.canReferenceUserInUpdate(userURN);
    }
}