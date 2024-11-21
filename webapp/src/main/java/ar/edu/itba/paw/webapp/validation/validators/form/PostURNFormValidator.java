package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNFormValidator implements ConstraintValidator<PostURNFormConstraint, String> {

    @Override
    public void initialize(PostURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext context) {
        if (postURN== null)
            return true;
        return URNValidator.validateURN(postURN, "posts");
    }
}
