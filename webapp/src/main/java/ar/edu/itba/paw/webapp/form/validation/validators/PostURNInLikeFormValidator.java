package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNInLikeFormConstraint;
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
        if (postURN == null)
            return false;

        return URNValidator.validateURN(postURN, "posts") && accessControlHelper.canReferencePostInLikeForm(postURN);
    }
}
