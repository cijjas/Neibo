package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.ImageConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ImageConstraint, byte[]> {

    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    @Override
    public void initialize(ImageConstraint imageConstraint) {
    }

    @Override
    public boolean isValid(byte[] image, ConstraintValidatorContext constraintValidatorContext) {
        if (image == null)
            return true;

        return image.length <= MAX_IMAGE_SIZE_BYTES;
    }
}
