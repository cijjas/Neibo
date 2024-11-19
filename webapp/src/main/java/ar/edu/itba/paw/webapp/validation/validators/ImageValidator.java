package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.ImageConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


// https://www.baeldung.com/spring-mvc-custom-validator
public class ImageValidator implements ConstraintValidator<ImageConstraint, MultipartFile> {
    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    @Override
    public void initialize(ImageConstraint imageConstraint) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return true;
        }

        if (multipartFile.getContentType().startsWith("image/")) {
            if (multipartFile.getSize() <= MAX_IMAGE_SIZE_BYTES) {
                return true;
            } else {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("ImageForm size exceeds the maximum allowed size")
                        .addConstraintViolation();
                return false;
            }
        }
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid image format")
                .addConstraintViolation();
        return false;
    }


}
