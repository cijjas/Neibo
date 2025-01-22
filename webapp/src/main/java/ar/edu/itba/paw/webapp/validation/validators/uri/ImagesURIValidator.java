package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ImagesURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ImagesURIValidator implements ConstraintValidator<ImagesURIConstraint, List<String>> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImagesURIConstraint imagesURIConstraint) {
    }

    @Override
    public boolean isValid(List<String> imageURIs, ConstraintValidatorContext constraintValidatorContext) {
        if (imageURIs == null)
            return true;
        for (String imageURI : imageURIs) {
            if (!URIValidator.validateURI(imageURI, Endpoint.IMAGES))
                return false;
            if (!imageService.findImage(extractFirstId(imageURI)).isPresent())
                return false;
        }
        return true;
    }
}