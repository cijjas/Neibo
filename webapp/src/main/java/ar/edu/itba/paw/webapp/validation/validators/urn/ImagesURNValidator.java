package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ImagesURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ImagesURNValidator implements ConstraintValidator<ImagesURNConstraint, List<String>> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImagesURNConstraint imagesURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> imagesURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (imagesURNs == null)
            return true;
        for (String imageURN : imagesURNs) {
            if (!URNValidator.validateURN(imageURN, Endpoint.IMAGES))
                return false;
            if (!imageService.findImage(extractFirstId(imageURN)).isPresent())
                return false;
        }
        return true;
    }
}