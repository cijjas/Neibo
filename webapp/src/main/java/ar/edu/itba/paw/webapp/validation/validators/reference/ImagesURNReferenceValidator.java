package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ImagesURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class ImagesURNReferenceValidator implements ConstraintValidator<ImagesURNReferenceConstraint, List<String>> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImagesURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> imagesURNs, ConstraintValidatorContext context) {
        if (imagesURNs == null)
            return true;
        for (String urn : imagesURNs)
            if (!imageService.findImage(extractFirstId(urn)).isPresent())
                return false;
        return true;
    }
}
