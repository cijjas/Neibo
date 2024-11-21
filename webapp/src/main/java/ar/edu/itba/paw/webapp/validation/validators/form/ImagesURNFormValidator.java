package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ImagesURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImagesURNFormValidator implements ConstraintValidator<ImagesURNFormConstraint, String[]> {

    @Override
    public void initialize(ImagesURNFormConstraint imagesURNConstraint) {}

    @Override
    public boolean isValid(String[] imagesURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (imagesURNs == null)
            return true;
        for (String urn : imagesURNs)
            if (!URNValidator.validateURN(urn, "images"))
                return false;
        return true;
    }
}