package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.reference.PostStatusURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class PostStatusURNReferenceValidator implements ConstraintValidator<PostStatusURNReferenceConstraint, String> {

    @Override
    public void initialize(PostStatusURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String postStatusURN, ConstraintValidatorContext context) {
        if (postStatusURN == null || postStatusURN.trim().isEmpty())
            return true;
        try {
            PostStatus.fromId(extractId(postStatusURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
