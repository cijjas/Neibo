package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.PostURNReferenceInLikeConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNReferenceInLikeValidator implements ConstraintValidator<PostURNReferenceInLikeConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(PostURNReferenceInLikeConstraint postURNInLikeFormConstraint) {
    }

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext constraintValidatorContext) {
        if (postURN == null)
            return true;
        return formAccessControlHelper.canReferencePostInLike(postURN);
    }
}
