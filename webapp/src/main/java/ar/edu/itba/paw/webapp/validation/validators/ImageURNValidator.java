package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.ImageURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageURNValidator implements ConstraintValidator<ImageURNConstraint, String> {

    @Override
    public void initialize(ImageURNConstraint imageURNConstraint) {
    }

    @Override
    public boolean isValid(String imageURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(imageURN, "images");

    }
}