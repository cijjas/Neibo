package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.PostURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class PostURNReferenceValidator implements ConstraintValidator<PostURNReferenceConstraint, String> {

    @Autowired
    private PostService postService;

    @Override
    public void initialize(PostURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext context) {
        if (postURN == null)
            return true;
        TwoId twoId = extractTwoId(postURN);
        return postService.findPost(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
