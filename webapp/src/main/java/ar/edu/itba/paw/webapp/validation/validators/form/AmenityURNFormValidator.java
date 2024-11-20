package ar.edu.itba.paw.webapp.validation.validators.form;


import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.AmenityURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmenityURNFormValidator implements ConstraintValidator<AmenityURNFormConstraint, String> {
    @Override
    public void initialize(AmenityURNFormConstraint amenityURNConstraint) {
    }

    @Override
    public boolean isValid(String amenityURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(amenityURN, "amenity");
    }
}
