package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.PostURNInLikeFormConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNInLikeFormValidator implements ConstraintValidator<PostURNInLikeFormConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(PostURNInLikeFormConstraint postURNInLikeFormConstraint) {
    }

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(postURN, "posts") && accessControlHelper.canReferencePostInLikeForm(postURN);
    }
}
