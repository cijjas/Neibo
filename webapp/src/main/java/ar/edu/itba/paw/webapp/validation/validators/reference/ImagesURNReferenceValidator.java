package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ImagesURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class ImagesURNReferenceValidator implements ConstraintValidator<ImagesURNReferenceConstraint, String[]> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImagesURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String[] imagesURNs, ConstraintValidatorContext context) {
        if (imagesURNs == null)
            return true;
        for (String urn : imagesURNs)
            if (!imageService.findImage(extractId(urn)).isPresent())
                return false;
        return true;
    }
}
