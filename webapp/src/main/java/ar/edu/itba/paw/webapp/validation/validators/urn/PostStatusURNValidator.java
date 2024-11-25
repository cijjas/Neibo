package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostStatusURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class PostStatusURNValidator implements ConstraintValidator<PostStatusURNConstraint, String> {

    @Override
    public void initialize(PostStatusURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String postStatusURN, ConstraintValidatorContext context) {
        if (postStatusURN == null)
            return true;
        if (!URNValidator.validateURN(postStatusURN, "post-status"))
            return false;
        try {
            PostStatus.fromId(extractFirstId(postStatusURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
