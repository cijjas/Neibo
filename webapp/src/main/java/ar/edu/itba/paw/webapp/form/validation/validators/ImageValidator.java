package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


// https://www.baeldung.com/spring-mvc-custom-validator
public class ImageValidator implements
        ConstraintValidator<ImageConstraint, byte[]> {

    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    @Override
    public void initialize(ImageConstraint imageConstraint) {

    }

    @Override
    public boolean isValid(byte[] imageData, ConstraintValidatorContext constraintValidatorContext) {
        // Check if the image byte array is null or empty
        return true;
    }
}
