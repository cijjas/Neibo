package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.PostURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class PostURIValidator implements ConstraintValidator<PostURIConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private PostService postService;

    @Override
    public void initialize(PostURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String postURI, ConstraintValidatorContext constraintValidatorContext) {
        if (postURI == null)
            return true;
        if (!URIValidator.validateURI(postURI, Endpoint.POSTS))
            return false;
        TwoId twoId = extractTwoId(postURI);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return postService.findPost(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
