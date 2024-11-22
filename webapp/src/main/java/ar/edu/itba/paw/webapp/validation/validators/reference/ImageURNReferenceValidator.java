package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ImageURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class ImageURNReferenceValidator implements ConstraintValidator<ImageURNReferenceConstraint, String> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImageURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String imageURN, ConstraintValidatorContext context) {
        if (imageURN == null || imageURN.trim().isEmpty())
            return true;
        Long id = extractOptionalFirstId(imageURN);
        return id == null || imageService.findImage(id).isPresent();
    }
}
