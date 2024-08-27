package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNValidator implements ConstraintValidator<PostURNConstraint, String> {

    @Override

    public void initialize(PostURNConstraint postURNConstraint) {
    }

    @Override

    public boolean isValid(String postURN, ConstraintValidatorContext constraintValidatorContext) {

        if (postURN == null)
            return false;

        return URNValidator.validateURN(postURN, "posts");

    }
}