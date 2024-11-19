package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.PostURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNValidator implements ConstraintValidator<PostURNConstraint, String> {

    @Override
    public void initialize(PostURNConstraint postURNConstraint) {
    }

    @Override

    public boolean isValid(String postURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(postURN, "posts");

    }
}