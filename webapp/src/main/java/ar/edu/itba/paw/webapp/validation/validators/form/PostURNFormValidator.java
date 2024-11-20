package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostURNFormValidator implements ConstraintValidator<PostURNFormConstraint, String> {
    @Override
    public void initialize(PostURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
