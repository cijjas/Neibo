package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.AmenityURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmenityURNReferenceValidator implements ConstraintValidator<AmenityURNReferenceConstraint, String> {
    @Override
    public void initialize(AmenityURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
