package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImageURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ImageURNValidator implements ConstraintValidator<ImageURNConstraint, String> {

    @Autowired
    private ImageService imageService;

    @Override
    public void initialize(ImageURNConstraint imageURNConstraint) {
    }

    @Override
    public boolean isValid(String imageURN, ConstraintValidatorContext constraintValidatorContext) {
        if (imageURN == null)
            return true;
        if (!URNValidator.validateURN(imageURN, "images"))
            return false;
        return imageService.findImage(extractFirstId(imageURN)).isPresent();
    }
}