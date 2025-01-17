package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.PostURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class PostURNValidator implements ConstraintValidator<PostURNConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private PostService postService;

    @Override
    public void initialize(PostURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext context) {
        if (postURN == null)
            return true;
        if (!URNValidator.validateURN(postURN, Endpoint.POSTS))
            return false;
        TwoId twoId = extractTwoId(postURN);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId()))
            return false;
        return postService.findPost(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
