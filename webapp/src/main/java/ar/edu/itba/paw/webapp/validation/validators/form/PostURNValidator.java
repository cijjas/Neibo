package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class PostURNValidator implements ConstraintValidator<PostURNConstraint, String> {

    @Autowired
    private PostService postService;

    @Override
    public void initialize(PostURNConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String postURN, ConstraintValidatorContext context) {
        if (postURN == null)
            return true;
        if (!URNValidator.validateURN(postURN, "posts"))
            return false;
        TwoId twoId = extractTwoId(postURN);
        return postService.findPost(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
