package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageURNValidator implements ConstraintValidator<ImageURNConstraint, String> {

    @Override
    public void initialize(ImageURNConstraint imageURNConstraint) {
    }

    @Override
    public boolean isValid(String imageURN, ConstraintValidatorContext constraintValidatorContext) {
        if (imageURN == null)
            return true;

        return URNValidator.validateURN(imageURN, "images");

    }
}