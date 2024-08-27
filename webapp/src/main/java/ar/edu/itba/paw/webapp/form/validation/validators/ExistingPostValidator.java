package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingPostConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ExistingPostValidator implements ConstraintValidator<ExistingPostConstraint, Long> {
    @Autowired
    PostService postService;

    @Override
    public void initialize(ExistingPostConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null || id <= 0)
            return false;
        return postService.findPost(id).isPresent();
    }

}
