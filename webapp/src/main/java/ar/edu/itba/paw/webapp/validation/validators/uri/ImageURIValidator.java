package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ImageURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ImageURIValidator implements ConstraintValidator<ImageURIConstraint, String> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImageURIConstraint imageURIConstraint) {
    }

    @Override
    public boolean isValid(String imageURI, ConstraintValidatorContext constraintValidatorContext) {
        if (imageURI == null)
            return true;
        if (!URIValidator.validateURI(imageURI, Endpoint.IMAGES))
            return false;
        return imageService.findImage(extractFirstId(imageURI)).isPresent();
    }
}