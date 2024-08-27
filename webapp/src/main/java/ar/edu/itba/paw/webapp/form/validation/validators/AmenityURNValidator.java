package ar.edu.itba.paw.webapp.form.validation.validators;


import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmenityURNValidator implements ConstraintValidator<AmenityURNConstraint, String> {
    @Override
    public void initialize(AmenityURNConstraint amenityURNConstraint) {
    }

    @Override
    public boolean isValid(String amenityURN, ConstraintValidatorContext constraintValidatorContext) {
        if (amenityURN == null)
            return false;
        return URNValidator.validateURN(amenityURN, "amenity");
    }
}
