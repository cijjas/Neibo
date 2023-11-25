package ar.edu.itba.paw.webapp.form.validation.validators;


import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.MultipleImagesConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class MultipleImagesValidator implements ConstraintValidator<MultipleImagesConstraint, MultipartFile[]> {

    private static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB


    @Override
    public void initialize(MultipleImagesConstraint multipleImagesConstraint) {

    }

    @Override
    public boolean isValid(MultipartFile[] multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFiles == null) {
            return true;
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.getContentType().startsWith("image/")) {
                if (multipartFile.getSize() > MAX_IMAGE_SIZE_BYTES) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Image size exceeds the maximum allowed size")
                            .addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }


}
