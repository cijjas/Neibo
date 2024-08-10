package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNAuthorizationValidator implements ConstraintValidator<UserURNAuthorizationConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNAuthorizationConstraint userURNAuthorizationConstraint) {}

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userURN == null)
            return true;
        return URNValidator.validateURN(userURN, "users") && accessControlHelper.canModify(userURN);
    }
}