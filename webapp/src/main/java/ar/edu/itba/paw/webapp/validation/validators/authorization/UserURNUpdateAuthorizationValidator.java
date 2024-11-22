package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.auth.PathAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceConstraintUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNUpdateAuthorizationValidator implements ConstraintValidator<UserURNReferenceConstraintUpdate, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserURNReferenceConstraintUpdate userURNReferenceConstraintUpdate) {}

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userURN == null)
            return true;
        return formAccessControlHelper.canReferenceUserInUpdate(userURN);
    }
}