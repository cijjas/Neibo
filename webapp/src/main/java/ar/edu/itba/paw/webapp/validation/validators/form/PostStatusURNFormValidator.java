package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostStatusURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostStatusURNFormValidator implements ConstraintValidator<PostStatusURNFormConstraint, String> {

    @Override
    public void initialize(PostStatusURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String postStatusURN, ConstraintValidatorContext context) {
        if (postStatusURN == null)
            return true;
        return URNValidator.validateURN(postStatusURN, "post-status");
    }
}
