package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNInLikeFormConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNInLikeFormValidator implements ConstraintValidator<UserURNInLikeFormConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNInLikeFormConstraint userURNInLikeFormConstraint) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userURN, "users") && accessControlHelper.canReferenceUserInLikeForm(userURN);
    }
}
