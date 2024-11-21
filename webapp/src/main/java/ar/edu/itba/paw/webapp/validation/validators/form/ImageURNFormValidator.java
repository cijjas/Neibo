package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImageURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageURNFormValidator implements ConstraintValidator<ImageURNFormConstraint, String> {

    @Override
    public void initialize(ImageURNFormConstraint imageURNConstraint) {}

    @Override
    public boolean isValid(String imageURN, ConstraintValidatorContext constraintValidatorContext) {
        if (imageURN == null)
            return true;
        return URNValidator.validateURN(imageURN, "images");
    }
}