package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.NeighborhoodURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodURNReferenceValidator implements ConstraintValidator<NeighborhoodURNReferenceConstraint, String> {
    @Override
    public void initialize(NeighborhoodURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
