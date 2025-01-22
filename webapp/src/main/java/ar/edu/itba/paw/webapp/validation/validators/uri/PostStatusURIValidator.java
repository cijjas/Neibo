package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.PostStatusURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class PostStatusURIValidator implements ConstraintValidator<PostStatusURIConstraint, String> {

    @Override
    public void initialize(PostStatusURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String postStatusURI, ConstraintValidatorContext context) {
        if (postStatusURI == null)
            return true;
        if (!URIValidator.validateURI(postStatusURI, Endpoint.POST_STATUSES))
            return false;
        try {
            PostStatus.fromId(extractFirstId(postStatusURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
