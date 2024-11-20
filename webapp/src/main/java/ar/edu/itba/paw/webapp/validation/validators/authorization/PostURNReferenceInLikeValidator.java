package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.PostURNReferenceInLikeConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNReferenceInLikeValidator implements ConstraintValidator<PostURNReferenceInLikeConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(PostURNReferenceInLikeConstraint postURNInLikeFormConstraint) {
    }

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(postURN, "posts") && accessControlHelper.canReferencePostInLikeForm(postURN);
    }
}
