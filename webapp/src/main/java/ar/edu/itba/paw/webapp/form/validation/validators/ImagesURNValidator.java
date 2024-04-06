package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImagesURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImagesURNValidator implements ConstraintValidator<ImagesURNConstraint, String[]> {

    @Override
    public void initialize(ImagesURNConstraint imagesURNConstraint) {}

    @Override
    public boolean isValid(String[] imagesURN, ConstraintValidatorContext constraintValidatorContext) {
        if(imagesURN==null)
            return true;

        for (String urn : imagesURN)
            if (!URNValidator.validateURN(urn, "images")) return false;

        return true;
    }
}