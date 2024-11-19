package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNInReviewFormConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNInReviewFormValidator implements ConstraintValidator<UserURNInReviewFormConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(UserURNInReviewFormConstraint userURNInReviewFormConstraint) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(userURN, "users") && accessControlHelper.canCreateReview(userURN);
    }
}
